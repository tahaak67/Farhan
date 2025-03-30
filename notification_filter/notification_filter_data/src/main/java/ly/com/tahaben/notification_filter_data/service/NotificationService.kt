package ly.com.tahaben.notification_filter_data.service

import android.app.Notification
import android.app.PendingIntent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ly.com.tahaben.notification_filter_data.mapper.toNotificationItem
import ly.com.tahaben.notification_filter_domain.use_cases.NotificationFilterUseCases
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : NotificationListenerService() {

    @Inject
    lateinit var notificationFilterUseCases: NotificationFilterUseCases

    companion object {
        private var intentHashmap: HashMap<String, PendingIntent> = HashMap()
        fun getIntentForNotification(key: String): PendingIntent? {

            return intentHashmap[key]
        }

        fun removeIntentFromHashmap(key: String) {
            intentHashmap.remove(key)
        }
    }


    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val isNotificationFilterEnabled = runBlocking { notificationFilterUseCases.checkIfNotificationServiceIsEnabled() }
        if (isNotificationFilterEnabled) {
            Timber.d("notification Posted")
            val notification = sbn?.notification
            val appPackageName = sbn?.packageName ?: ""
            val extras = notification?.extras
            val title = extras?.getString(Notification.EXTRA_TITLE)?.toString()
            val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            if (notification?.category != Notification.CATEGORY_SERVICE &&
                notification?.category != Notification.CATEGORY_CALL &&
                notification?.category != Notification.CATEGORY_ALARM &&
                notification?.category != Notification.CATEGORY_MISSED_CALL &&
                notification?.category != Notification.CATEGORY_SYSTEM
            ) {
                notification?.contentIntent?.let {
                    intentHashmap.put(sbn.key, it)
                }
                if (notificationFilterUseCases.isPackageInNotificationException(appPackageName) ||
                    appPackageName == this.packageName
                ) {
                    super.onNotificationPosted(sbn)
                } else {
                    cancelNotification(sbn?.key)
                    val pm: PackageManager = this.packageManager
                    val ai: ApplicationInfo? = try {
                        pm.getApplicationInfo(appPackageName, 0)
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                        null
                    }
                    val applicationName =
                        (if (ai != null) pm.getApplicationLabel(ai) else appPackageName) as String?
                    runBlocking(Dispatchers.IO) {
                        sbn?.toNotificationItem(applicationName)
                            ?.let { notificationFilterUseCases.insertNotificationToDB(it) }
                    }
                    if (notificationFilterUseCases.getNotifyMeHour() != -1) {
                        notificationFilterUseCases.createNotifyMeNotificationChannel()
                        notificationFilterUseCases.scheduleNotifyMeNotification(
                            notificationFilterUseCases.getNotifyMeHour(),
                            notificationFilterUseCases.getNotifyMeMinute()
                        )
                    }
                }

            }
            Timber.d(" ${sbn?.packageName} title=:${title} text=:$text category: ${notification?.category}")
        }

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Timber.d("notification removed")
        val notification = sbn?.notification
        val extras = notification?.extras
        val title = extras?.getString(Notification.EXTRA_TITLE)
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val progress = extras?.getInt(Notification.EXTRA_PROGRESS)
        val progressMax = extras?.getInt(Notification.EXTRA_PROGRESS_MAX)
        val progressIndeterminate = extras?.getBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE)

        Timber.d(" ${sbn?.packageName} title=:${title} text=:$text progress: $progress progressMax: $progressMax progressIndeterminate: $progressIndeterminate")

        super.onNotificationRemoved(sbn)
    }

    override fun onDestroy() {
        intentHashmap.clear()
        super.onDestroy()
    }
}