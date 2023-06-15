package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.preferences.Preferences

class GetEnabledUsageReports(private val sharedPref: Preferences) {
    operator fun invoke(): Map<String, Boolean> {
        return sharedPref.getEnabledUsageReports()
    }
}
