package ly.com.tahaben.notification_filter_domain.use_cases

import kotlinx.coroutines.flow.Flow
import ly.com.tahaben.notification_filter_domain.model.NotificationItem
import ly.com.tahaben.notification_filter_domain.repository.NotificationRepository

class GetNotificationsFromDB(
    private val repository: NotificationRepository
) {

    operator fun invoke(): Flow<List<NotificationItem>> {
        return repository.getNotifications()
    }
}