package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.preferences.Preferences

class SetCachingEnabled(private val sharedPref: Preferences) {
    operator fun invoke(isEnabled: Boolean) {
        sharedPref.setCacheEnabled(isEnabled)
    }
}
