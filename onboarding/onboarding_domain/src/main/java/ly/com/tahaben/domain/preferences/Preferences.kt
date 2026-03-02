package ly.com.tahaben.domain.preferences

import kotlinx.coroutines.flow.Flow

interface Preferences {

    fun loadShouldShowOnBoarding(): Boolean
    fun saveShouldShowOnBoarding(shouldShow: Boolean)

    suspend fun loadDarkModeOn(): String
    suspend fun saveDarkModeOn(darkMode: String)

    suspend fun loadThemeColors(): String
    suspend fun saveThemeColors(themeColors: String)

    suspend fun loadMainSwitchState(): Flow<Boolean>
    suspend fun setMainSwitchState(switchState: Boolean)

    fun loadShouldShowcaseAppearanceMenu(): Boolean
    fun saveShouldShowcaseAppearanceMenu(shouldShowcase: Boolean)

    fun loadShouldCombineDb(): Boolean
    fun saveShouldCombineDb(shouldCombine: Boolean)

    fun loadDarkModeStateAsFlow(): Flow<String>
    fun loadThemeColorsAsFlow(): Flow<String>

    companion object {
        const val KEY_APP_SHOULD_SHOW_ON_BOARDING =
            "app_should_show_on_boarding"
        const val KEY_APP_DARK_MODE_ON = "app_dark_mode_on"
        const val KEY_APP_THEME_COLORS = "app_theme_colors"
        const val KEY_SHOULD_SHOWCASE_APPEARANCE_MENU = "should_show_appearance_menu"
        const val KEY_SHOULD_COMBINE_DB = "should_combine_db"
    }
}