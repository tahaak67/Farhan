package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.model.NotificationItem
import ly.com.tahaben.notification_filter_domain.repository.NotificationRepository

class InsertNotificationToDB(
    private val repository: NotificationRepository
) {

    suspend operator fun invoke(notificationItem: NotificationItem) {
        repository.insertNotificationItemEntity(notificationItem)
    }
}