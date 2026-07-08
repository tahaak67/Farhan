package ly.com.tahaben.notification_filter_domain.use_cases.settings

import ly.com.tahaben.notification_filter_domain.preferences.Preferences
import java.time.LocalTime

class SetFilterScheduleEndTime(
    private val sharedPref: Preferences
) {

    operator fun invoke(time: LocalTime) {
        sharedPref.setFilterScheduleEndTime(time)
    }
}
