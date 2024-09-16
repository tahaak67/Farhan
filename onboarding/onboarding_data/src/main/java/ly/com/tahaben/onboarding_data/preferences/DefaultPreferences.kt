
package ly.com.tahaben.onboarding_data.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core.util.GlobalKey
import ly.com.tahaben.domain.preferences.Preferences

class DefaultPreferences(
    private val sharedPref: SharedPreferences,
    private val context: Context
) : Preferences {

    override fun loadShouldShowOnBoarding(): Boolean {
        return sharedPref.getBoolean(Preferences.KEY_APP_SHOULD_SHOW_ON_BOARDING, true)
    }

    @SuppressLint("ApplySharedPref")
    override fun saveShouldShowOnBoarding(shouldShow: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.KEY_APP_SHOULD_SHOW_ON_BOARDING, shouldShow)
            .commit()
    }

    override fun loadDarkModeOn(): String {
        return sharedPref.getString(
            Preferences.KEY_APP_DARK_MODE_ON,
            UIModeAppearance.FOLLOW_SYSTEM.name
        )
            ?: UIModeAppearance.FOLLOW_SYSTEM.name
    }

    override fun saveDarkModeOn(darkMode: String) {
        sharedPref.edit()
            .putString(Preferences.KEY_APP_DARK_MODE_ON, darkMode)
            .apply()
    }

    override fun loadMainSwitchState(): Boolean {
        return sharedPref.getBoolean(GlobalKey.Pref_KEY_APP_MAIN_SWITCH, true)
    }

    override fun setMainSwitchState(switchState: Boolean) {
        sharedPref.edit()
            .putBoolean(GlobalKey.Pref_KEY_APP_MAIN_SWITCH, switchState)
            .apply()
    }

    override fun loadThemeColors(): String {
        return sharedPref.getString(
            Preferences.KEY_APP_THEME_COLORS,
            "Unknown"
        ) ?: "Unknown"
    }

    override fun saveThemeColors(themeColors: String) {
        sharedPref.edit()
            .putString(Preferences.KEY_APP_THEME_COLORS, themeColors)
            .apply()
    }

    override fun loadShouldShowcaseAppearanceMenu(): Boolean {
        return sharedPref.getBoolean(
            Preferences.KEY_SHOULD_SHOWCASE_APPEARANCE_MENU,
            true
        )
    }

    override fun saveShouldShowcaseAppearanceMenu(shouldShowcase: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.KEY_SHOULD_SHOWCASE_APPEARANCE_MENU, shouldShowcase)
            .apply()
    }

    override fun loadShouldCombineDb(): Boolean {
        val dbList = context.databaseList().toList()
        val oldDbsExist = dbList.contains("notifications_db") || dbList.contains("usage_db")
        return sharedPref.getBoolean(
            Preferences.KEY_SHOULD_COMBINE_DB,
            true
        ) && oldDbsExist
    }

    override fun saveShouldCombineDb(shouldCombine: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.KEY_SHOULD_COMBINE_DB, shouldCombine)
            .apply()
    }
}