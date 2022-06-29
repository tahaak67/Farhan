package ly.com.tahaben.notification_filter_domain.use_cases.settings

import ly.com.tahaben.notification_filter_domain.preferences.Preferences

class SetNotifyMeScheduleDate(
    private val sharedPref: Preferences
) {

    operator fun invoke(date: Long) {
        sharedPref.setNotifyMeScheduledDate(date)
    }
}