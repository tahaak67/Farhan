package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.util.ServiceUtil

class CheckIfNotificationServiceIsEnabled(
    private val serviceUtil: ServiceUtil
) {
    suspend operator fun invoke(): Boolean {
        return serviceUtil.isNotificationServiceEnabled()
    }
}