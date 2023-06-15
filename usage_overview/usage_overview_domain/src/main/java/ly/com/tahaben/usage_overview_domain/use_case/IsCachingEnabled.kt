package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.preferences.Preferences

class IsCachingEnabled(private val sharedPref: Preferences) {
    operator fun invoke(): Boolean {
        return sharedPref.isCacheEnabled()
    }
}
