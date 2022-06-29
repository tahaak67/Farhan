package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.util.ServiceUtil

class ScheduleNotifyMeNotification(
    private val serviceUtil: ServiceUtil
) {
    operator fun invoke(hour: Int, minute: Int) {
        serviceUtil.scheduleNotifyMeNotification(hour, minute)
    }
}