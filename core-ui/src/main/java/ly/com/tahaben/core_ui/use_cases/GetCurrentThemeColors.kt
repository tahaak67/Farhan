package ly.com.tahaben.core_ui.use_cases

import android.content.SharedPreferences
import ly.com.tahaben.core.data.preferences.UiPreferencesConstants
import ly.com.tahaben.core.model.ThemeColors

class GetCurrentThemeColors(
    private val sharedPref: SharedPreferences
) {

    operator fun invoke(): ThemeColors {
        val result = sharedPref.getString(
            UiPreferencesConstants.KEY_APP_THEME_COLORS,
            "Unknown"
        ) ?: "Unknown"
        return when (result) {
            ThemeColors.Vibrant.name -> ThemeColors.Vibrant
            ThemeColors.Dynamic.name -> ThemeColors.Dynamic
            else -> ThemeColors.Classic
        }
    }

}
