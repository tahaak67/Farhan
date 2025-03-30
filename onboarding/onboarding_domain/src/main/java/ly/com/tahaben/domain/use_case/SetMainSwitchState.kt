package ly.com.tahaben.domain.use_case

import ly.com.tahaben.domain.preferences.Preferences

class SetMainSwitchState(private val preferences: Preferences) {
    suspend operator fun invoke(isEnabled: Boolean) {
        preferences.setMainSwitchState(isEnabled)
    }
}
