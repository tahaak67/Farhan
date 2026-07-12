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

enum class RunningService {
    ACCESSIBILITY,
    NOTIFICATION_LISTENER,
    TIME_LIMITER
}

enum class ActiveFeature(
    @param:StringRes val displayNameRes: Int,
    val hostService: RunningService,
    /**
     * True when the host service only runs while the feature is on, so the service being up
     * is itself the "enabled" signal and no preference toggle is tracked for it.
     */
    val impliedByHostService: Boolean = false
) {
    NOTIFICATION_FILTER(
        R.string.active_feature_notification_filter,
        RunningService.NOTIFICATION_LISTENER
    ),
    GRAYSCALE(R.string.active_feature_grayscale, RunningService.ACCESSIBILITY),
    INFINITE_SCROLL_BLOCKER(
        R.string.active_feature_infinite_scroll,
        RunningService.ACCESSIBILITY
    ),
    DELAYED_LAUNCH(R.string.active_feature_delayed_launch, RunningService.ACCESSIBILITY),
    DELAYED_UNLOCK(R.string.active_feature_delayed_unlock, RunningService.ACCESSIBILITY),
    TIME_LIMITER(
        R.string.active_feature_time_limiter,
        RunningService.TIME_LIMITER,
        impliedByHostService = true
    )
}

/**
 * Keeps a single ongoing notification listing the features that are actually working right now:
 * a feature is shown only while its toggle is on (which already implies the app main switch is
 * on) AND the service that powers it is running. A service running with every one of its
 * features switched off shows nothing, so the notification never claims Farhan is doing
 * something it isn't.
 *
 * Services report themselves via [serviceStarted]/[serviceStopped]; the enabled-feature set is
 * pushed from the app module's ActiveFeaturesTracker, which watches the preference toggles.
 *
 * The time limit foreground service must reuse [NOTIFICATION_ID] and [buildNotification] for
 * its startForeground call so the user only ever sees one "running" notification.
 */
@Singleton
class RunningServicesNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val runningServices = mutableSetOf<RunningService>()
    private val enabledFeatures = mutableSetOf<ActiveFeature>()

    @Synchronized
    fun serviceStarted(service: RunningService) {
        if (runningServices.add(service)) {
            refreshNotification()
        }
    }

    @Synchronized
    fun serviceStopped(service: RunningService) {
        if (runningServices.remove(service)) {
            refreshNotification()
        }
    }

    @Synchronized
    fun setEnabledFeatures(features: Set<ActiveFeature>) {
        if (enabledFeatures == features) return
        enabledFeatures.clear()
        enabledFeatures.addAll(features)
        refreshNotification()
    }

    fun buildNotification(): Notification {
        createChannel()
        val pendingIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?.let { PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_IMMUTABLE) }
        val featureNames = activeFeatures().joinToString { context.getString(it.displayNameRes) }
        return NotificationCompat.Builder(
            context,
            context.getString(R.string.running_services_channel_id)
        )
            .setSmallIcon(R.drawable.ic_farhan_transparent)
            .setContentTitle(context.getString(R.string.running_services_notification_title))
            .setContentText(
                context.getString(R.string.running_services_notification_text, featureNames)
            )
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @Synchronized
    private fun activeFeatures(): List<ActiveFeature> =
        ActiveFeature.entries.filter { feature ->
            feature.hostService in runningServices &&
                    (feature.impliedByHostService || feature in enabledFeatures)
        }

    private fun refreshNotification() {
        if (activeFeatures().isEmpty()) {
            notificationManager.cancel(NOTIFICATION_ID)
        } else {
            // Always re-post rather than only cancel: stopForeground(true) in the time limit
            // service removes the shared notification even when other features remain active.
            showOrUpdateNotification()
        }
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
