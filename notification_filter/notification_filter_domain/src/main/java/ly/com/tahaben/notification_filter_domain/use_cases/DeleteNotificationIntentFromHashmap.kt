package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.util.ServiceUtil

class DeleteNotificationIntentFromHashmap(
    private val serviceUtil: ServiceUtil
) {

    operator fun invoke(notificationKey: String) {
        serviceUtil.deleteNotificationIntent(notificationKey)
    }
}