package ly.com.tahaben.domain.use_case

import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.domain.preferences.Preferences

class GetThemeColorsPreference(private val preferences: Preferences) {
    operator fun invoke(): ThemeColors? {
        val themeColors = preferences.loadThemeColors()
        return when (themeColors) {
            ThemeColors.Classic.name -> ThemeColors.Classic
            ThemeColors.Vibrant.name -> ThemeColors.Vibrant
            ThemeColors.Dynamic.name -> ThemeColors.Dynamic
            else -> null
        }
    }
}
