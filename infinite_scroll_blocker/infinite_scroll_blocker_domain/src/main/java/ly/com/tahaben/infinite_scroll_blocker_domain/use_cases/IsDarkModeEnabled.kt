package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences

class IsDarkModeEnabled(private val preferences: Preferences) {
    operator fun invoke(): UIModeAppearance {
        return when (preferences.loadDarkModeOn()) {
            UIModeAppearance.DARK_MODE.name -> UIModeAppearance.DARK_MODE
            UIModeAppearance.LIGHT_MODE.name -> UIModeAppearance.LIGHT_MODE
            else -> UIModeAppearance.FOLLOW_SYSTEM
        }
    }
}
