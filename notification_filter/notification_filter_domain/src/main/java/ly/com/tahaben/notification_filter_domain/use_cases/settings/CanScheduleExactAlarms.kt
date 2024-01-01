package ly.com.tahaben.notification_filter_domain.use_cases.settings

import ly.com.tahaben.notification_filter_domain.util.ServiceUtil

class CanScheduleExactAlarms(private val notificationFilerService: ServiceUtil) {
    operator fun invoke(): Boolean {
        return notificationFilerService.canScheduleExactAlarms()
    }
}
