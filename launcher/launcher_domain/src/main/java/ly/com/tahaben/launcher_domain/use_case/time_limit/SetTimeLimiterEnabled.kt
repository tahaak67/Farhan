package ly.com.tahaben.launcher_domain.use_case.time_limit

import ly.com.tahaben.launcher_domain.preferences.Preference

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 18,Feb,2023
 */
class SetTimeLimiterEnabled(private val sharedPref: Preference) {
    operator fun invoke(isEnabled: Boolean) {
        sharedPref.setTimeLimiterEnabled(isEnabled)
    }
}