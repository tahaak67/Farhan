package ly.com.tahaben.notification_filter_domain.use_cases.settings

import ly.com.tahaben.notification_filter_domain.preferences.Preferences

class SetFilterScheduleEnabled(
    private val sharedPref: Preferences
) {

    operator fun invoke(isEnabled: Boolean) {
        sharedPref.setFilterScheduleEnabled(isEnabled)
    }
}
