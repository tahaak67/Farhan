package ly.com.tahaben.core.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ly.com.tahaben.core.R
import ly.com.tahaben.core.service.RunningServicesNotifier.Companion.NOTIFICATION_ID
import javax.inject.Inject
import javax.inject.Singleton

enum class RunningService(@param:StringRes val displayNameRes: Int) {
    ACCESSIBILITY(R.string.running_service_accessibility),
    NOTIFICATION_FILTER(R.string.running_service_notification_filter),
    TIME_LIMITER(R.string.running_service_time_limiter)
}

/**
 * Tracks which of the app's background services are currently running and keeps a single
 * ongoing notification visible while at least one of them is.
 *
 * The time limit foreground service must reuse [NOTIFICATION_ID] and [buildNotification] for
 * its startForeground call so the user only ever sees one "running" notification.
 */
@Singleton
class RunningServicesNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val runningServices = linkedSetOf<RunningService>()

    @Synchronized
    fun serviceStarted(service: RunningService) {
        if (runningServices.add(service)) {
            showOrUpdateNotification()
        }
    }

    @Synchronized
    fun serviceStopped(service: RunningService) {
        if (runningServices.remove(service)) {
            if (runningServices.isEmpty()) {
                notificationManager.cancel(NOTIFICATION_ID)
            } else {
                // Re-post rather than only cancel: stopForeground(true) in the time limit
                // service removes the shared notification even when other services remain.
                showOrUpdateNotification()
            }
        }
    }

    fun buildNotification(): Notification {
        createChannel()
        val pendingIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?.let { PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_IMMUTABLE) }
        val serviceNames = synchronized(this) {
            runningServices.joinToString { context.getString(it.displayNameRes) }
        }
        return NotificationCompat.Builder(
            context,
            context.getString(R.string.running_services_channel_id)
        )
            .setSmallIcon(R.drawable.ic_farhan_transparent)
            .setContentTitle(context.getString(R.string.running_services_notification_title))
            .setContentText(
                context.getString(R.string.running_services_notification_text, serviceNames)
            )
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun showOrUpdateNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                context.getString(R.string.running_services_channel_id),
                context.getString(R.string.running_services_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.running_services_channel_description)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private val notificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val NOTIFICATION_ID = 110
    }
}
