package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.util.ServiceUtil

class CheckIfNotificationAccessIsGranted(
    private val serviceUtil: ServiceUtil
) {
    operator fun invoke(): Boolean {
        return serviceUtil.isNotificationAccessPermissionGranted()
    }
}