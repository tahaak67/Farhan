
package ly.com.tahaben.onboarding_data.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core.util.GlobalKey
import ly.com.tahaben.domain.preferences.Preferences

class DefaultPreferences(
    private val sharedPref: SharedPreferences,
    private val context: Context,
    private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>
) : Preferences {
    // data store keys
    private val uiAppearanceKey = stringPreferencesKey(Preferences.KEY_APP_DARK_MODE_ON)
    private val themeColorsKey = stringPreferencesKey(Preferences.KEY_APP_THEME_COLORS)

    override fun loadShouldShowOnBoarding(): Boolean {
        return sharedPref.getBoolean(Preferences.KEY_APP_SHOULD_SHOW_ON_BOARDING, true)
    }

    @SuppressLint("ApplySharedPref")
    override fun saveShouldShowOnBoarding(shouldShow: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.KEY_APP_SHOULD_SHOW_ON_BOARDING, shouldShow)
            .commit()
    }

    override suspend fun loadDarkModeOn(): String {
        return dataStore.data.first()[uiAppearanceKey] ?: UIModeAppearance.FOLLOW_SYSTEM.name
    }

    override suspend fun saveDarkModeOn(darkMode: String) {
        dataStore.edit { prefs ->
            prefs[uiAppearanceKey] = darkMode
        }

    }

    override fun loadMainSwitchState(): Boolean {
        return sharedPref.getBoolean(GlobalKey.Pref_KEY_APP_MAIN_SWITCH, true)
    }

    override fun setMainSwitchState(switchState: Boolean) {
        sharedPref.edit()
            .putBoolean(GlobalKey.Pref_KEY_APP_MAIN_SWITCH, switchState)
            .apply()
    }

    override suspend fun loadThemeColors(): String {
       return dataStore.data.first()[themeColorsKey] ?: "Unknown"

    }

    override suspend fun saveThemeColors(themeColors: String) {
        dataStore.edit { prefs ->
            prefs[themeColorsKey] = themeColors
        }
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

    override fun loadDarkModeStateAsFlow(): Flow<String> {
       return dataStore.data.map { prefs ->
           prefs[uiAppearanceKey] ?: UIModeAppearance.FOLLOW_SYSTEM.name
       }
    }

    override fun loadThemeColorsAsFlow(): Flow<String> {
        return dataStore.data.map { prefs ->
            prefs[themeColorsKey] ?: ThemeColors.Classic.name
        }
    }
}