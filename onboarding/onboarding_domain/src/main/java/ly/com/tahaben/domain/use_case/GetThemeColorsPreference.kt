package ly.com.tahaben.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.domain.preferences.Preferences

class GetThemeColorsPreference(private val preferences: Preferences) {
    operator fun invoke(): Flow<ThemeColors?> {
        return preferences.loadThemeColorsAsFlow().map { themeColors ->
            when (themeColors) {
                ThemeColors.Classic.name -> ThemeColors.Classic
                ThemeColors.Vibrant.name -> ThemeColors.Vibrant
                ThemeColors.Dynamic.name -> ThemeColors.Dynamic
                else -> null
            }
        }

    }
}
