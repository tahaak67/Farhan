package ly.com.tahaben.notification_filter_domain.use_cases.settings

import ly.com.tahaben.notification_filter_domain.preferences.Preferences

class SetNotifyMeTime(
    private val sharedPref: Preferences
) {
    operator fun invoke(hour: Int, minutes: Int) {
        sharedPref.setNotifyMeTime(hour, minutes)
    }
}