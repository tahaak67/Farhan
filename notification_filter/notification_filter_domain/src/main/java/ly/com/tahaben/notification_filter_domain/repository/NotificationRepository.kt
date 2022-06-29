package ly.com.tahaben.notification_filter_domain.repository

import kotlinx.coroutines.flow.Flow
import ly.com.tahaben.notification_filter_domain.model.NotificationItem

interface NotificationRepository {

    suspend fun insertNotificationItemEntity(notificationItem: NotificationItem)
    suspend fun deleteNotificationItemEntity(notificationItem: NotificationItem)
    suspend fun deleteAllNotificationsFromDB()
    fun getNotifications(): Flow<List<NotificationItem>>

}