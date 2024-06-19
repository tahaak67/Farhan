package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences

class GetCurrentThemeColors(private val preferences: Preferences) {

    operator fun invoke(): ThemeColors {
        return when (preferences.loadThemeColors()) {
            ThemeColors.Vibrant.name -> ThemeColors.Vibrant
            ThemeColors.Dynamic.name -> ThemeColors.Dynamic
            else -> ThemeColors.Classic
        }
    }

}
