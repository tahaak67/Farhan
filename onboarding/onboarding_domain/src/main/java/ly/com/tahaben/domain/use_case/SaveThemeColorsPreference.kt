package ly.com.tahaben.domain.use_case

import ly.com.tahaben.core.util.ThemeColors
import ly.com.tahaben.domain.preferences.Preferences

class SaveThemeColorsPreference(private val preferences: Preferences) {
    operator fun invoke(themeColors: ThemeColors) {
        when (themeColors) {
            ThemeColors.Vibrant -> preferences.saveThemeColors(ThemeColors.Vibrant.name)
            ThemeColors.Classic -> preferences.saveThemeColors(ThemeColors.Classic.name)
            ThemeColors.Dynamic -> preferences.saveThemeColors(ThemeColors.Dynamic.name)
        }
    }

}
