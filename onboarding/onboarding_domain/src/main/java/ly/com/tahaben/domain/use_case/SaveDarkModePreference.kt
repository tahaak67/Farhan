package ly.com.tahaben.domain.use_case

import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core.model.UIModeAppearance.DARK_MODE
import ly.com.tahaben.core.model.UIModeAppearance.FOLLOW_SYSTEM
import ly.com.tahaben.core.model.UIModeAppearance.LIGHT_MODE
import ly.com.tahaben.domain.preferences.Preferences

class SaveDarkModePreference(
    private val preferences: Preferences
) {
    operator suspend fun invoke(uiModeAppearance: UIModeAppearance) {
        when (uiModeAppearance) {
            DARK_MODE -> preferences.saveDarkModeOn(DARK_MODE.name)
            LIGHT_MODE -> preferences.saveDarkModeOn(LIGHT_MODE.name)
            FOLLOW_SYSTEM -> preferences.saveDarkModeOn(FOLLOW_SYSTEM.name)
        }
    }
}