package ly.com.tahaben.domain.use_case

import ly.com.tahaben.domain.preferences.Preferences

class IsMainSwitchState(private val preferences: Preferences) {
    operator fun invoke(): Boolean {
        return preferences.loadMainSwitchState()
    }
}
