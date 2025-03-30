package ly.com.tahaben.core_ui.use_cases

import android.content.SharedPreferences
import ly.com.tahaben.core.data.preferences.UiPreferencesConstants
import ly.com.tahaben.core.model.UIModeAppearance

class IsDarkModeEnabled(
    private val sharedPref: SharedPreferences
) {
    operator fun invoke(): UIModeAppearance {
        val result = sharedPref.getString(
            UiPreferencesConstants.KEY_APP_DARK_MODE_ON,
            UIModeAppearance.FOLLOW_SYSTEM.name
        )
            ?: UIModeAppearance.FOLLOW_SYSTEM.name
        return when (result) {
            UIModeAppearance.DARK_MODE.name -> UIModeAppearance.DARK_MODE
            UIModeAppearance.LIGHT_MODE.name -> UIModeAppearance.LIGHT_MODE
            else -> UIModeAppearance.FOLLOW_SYSTEM
        }
    }
}
