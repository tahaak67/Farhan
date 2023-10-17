package ly.com.tahaben.domain.use_case

import ly.com.tahaben.domain.model.UIModeAppearance
import ly.com.tahaben.domain.model.UIModeAppearance.DARK_MODE
import ly.com.tahaben.domain.model.UIModeAppearance.FOLLOW_SYSTEM
import ly.com.tahaben.domain.model.UIModeAppearance.LIGHT_MODE
import ly.com.tahaben.domain.preferences.Preferences

class GetDarkModePreference(
    private val preferences: Preferences
) {
    operator fun invoke(): UIModeAppearance {
        val uiMode = preferences.loadDarkModeOn()
        return when (uiMode) {
            UIModeAppearance.DARK_MODE.name -> DARK_MODE
            UIModeAppearance.LIGHT_MODE.name -> LIGHT_MODE
            else -> FOLLOW_SYSTEM
        }
    }
}