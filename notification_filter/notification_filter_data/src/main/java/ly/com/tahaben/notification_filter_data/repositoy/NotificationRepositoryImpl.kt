package ly.com.tahaben.notification_filter_data.repositoy

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ly.com.tahaben.notification_filter_data.local.NotificationDao
import ly.com.tahaben.notification_filter_data.mapper.toNotificationEntity
import ly.com.tahaben.notification_filter_data.mapper.toNotificationItem
import ly.com.tahaben.notification_filter_domain.model.NotificationItem
import ly.com.tahaben.notification_filter_domain.repository.NotificationRepository

class NotificationRepositoryImpl(private val dao: NotificationDao) : NotificationRepository {

    override suspend fun insertNotificationItemEntity(notificationItem: NotificationItem) {
        dao.insertNotificationItem(notificationItem.toNotificationEntity())
    }

    override suspend fun deleteNotificationItemEntity(notificationItem: NotificationItem) {
        dao.deleteNotificationItem(notificationItem.toNotificationEntity())
    }

    override suspend fun deleteAllNotificationsFromDB() {
        dao.deleteAllNotifications()
    }

    override fun getNotifications(): Flow<List<NotificationItem>> {
        return dao.getNotifications()
            .map { entities ->
                entities.map { it.toNotificationItem() }
            }
    }

}