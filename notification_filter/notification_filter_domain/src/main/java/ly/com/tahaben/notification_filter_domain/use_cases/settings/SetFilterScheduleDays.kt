package ly.com.tahaben.notification_filter_domain.use_cases.settings

import ly.com.tahaben.notification_filter_domain.preferences.Preferences
import java.time.DayOfWeek

class SetFilterScheduleDays(
    private val sharedPref: Preferences
) {

    operator fun invoke(days: Set<DayOfWeek>) {
        sharedPref.setFilterScheduleDays(days)
    }
}
