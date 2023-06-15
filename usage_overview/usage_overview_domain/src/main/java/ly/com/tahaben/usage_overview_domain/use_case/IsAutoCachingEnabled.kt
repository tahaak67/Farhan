package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.preferences.Preferences

class IsAutoCachingEnabled(private val sharePref: Preferences) {
    operator fun invoke(): Boolean {
        return sharePref.isAutoCacheEnabled()
    }
}
