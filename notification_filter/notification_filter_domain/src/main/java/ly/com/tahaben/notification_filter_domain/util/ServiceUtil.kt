package ly.com.tahaben.notification_filter_domain.util

import android.app.PendingIntent

interface ServiceUtil {

    fun isNotificationListenerServiceEnabled(): Boolean
    fun startNotificationListenerService()
    fun getNotificationIntent(notificationKey: String): PendingIntent?
    fun deleteNotificationIntent(notificationKey: String)
    fun createNotifyMeNotificationChannel()
    fun scheduleNotifyMeNotification(hour: Int, minute: Int)
}