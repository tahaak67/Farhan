package ly.com.tahaben.notification_filter_domain.use_cases.settings

import ly.com.tahaben.notification_filter_domain.util.ServiceUtil

class OpenSettings(private val notificationServiceUtil: ServiceUtil) {
    operator fun invoke() {
        notificationServiceUtil.openAppSettings()
    }
}
