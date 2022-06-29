package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.util.ServiceUtil

class StartNotificationService(
    private val serviceUtil: ServiceUtil
) {
    operator fun invoke() {
        return serviceUtil.startNotificationListenerService()
    }
}