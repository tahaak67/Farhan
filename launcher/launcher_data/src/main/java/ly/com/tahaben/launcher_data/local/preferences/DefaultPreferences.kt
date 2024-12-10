package ly.com.tahaben.launcher_data.local.preferences

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.last
import ly.com.tahaben.launcher_data.local.Constants
import ly.com.tahaben.launcher_domain.preferences.Preference
import timber.log.Timber

class DefaultPreferences(
    private val sharedPref: SharedPreferences,
    private val dataStore: DataStore<Preferences>
) : Preference {

    override fun isLauncherEnabled(): Boolean {
        return sharedPref.getBoolean(Preference.KEY_LAUNCHER_ENABLED, false)
    }

    override fun setLauncherEnabled(isEnabled: Boolean) {
        sharedPref.edit()
            .putBoolean(Preference.KEY_LAUNCHER_ENABLED, isEnabled)
            .apply()
    }

    override fun isTimeLimiterEnabled(): Boolean {
        return sharedPref.getBoolean(Preference.KEY_TIME_LIMITER_ENABLED, false)
    }

    override fun setTimeLimiterEnabled(isEnabled: Boolean) {
        sharedPref.edit()
            .putBoolean(Preference.KEY_TIME_LIMITER_ENABLED, isEnabled)
            .apply()
    }

    override fun addPackageToTimeLimitPackages(packageName: String) {
        val savedSet: MutableSet<String>? =
            sharedPref.getStringSet(
                Preference.KEY_TIME_LIMIT_PACKAGES,
                Constants.DEFAULT_TIME_LIMITED_APPS
            )
        Timber.d("old set: $savedSet")
        val newSet = savedSet?.toMutableSet()
        newSet?.add(packageName)
        Timber.d("new set: $newSet")
        sharedPref.edit()
            .putStringSet(Preference.KEY_TIME_LIMIT_PACKAGES, newSet)
            .apply()
    }

    override fun removePackageFromTimeLimitPackages(packageName: String) {
        val savedSet: MutableSet<String>? =
            sharedPref.getStringSet(
                Preference.KEY_TIME_LIMIT_PACKAGES,
                Constants.DEFAULT_TIME_LIMITED_APPS
            )
        Timber.d("old set: $savedSet")
        val newSet = savedSet?.toMutableSet()
        newSet?.remove(packageName)
        Timber.d("new set: $newSet")
        sharedPref.edit()
            .putStringSet(Preference.KEY_TIME_LIMIT_PACKAGES, newSet)
            .apply()
    }

    override fun isPackageInTimeLimitPackages(packageName: String): Boolean {
        val set = sharedPref.getStringSet(
            Preference.KEY_TIME_LIMIT_PACKAGES,
            Constants.DEFAULT_TIME_LIMITED_APPS
        )
        return set?.contains(packageName) == true
    }
    val keyPackageInMLWhiteList = stringSetPreferencesKey(Preference.MINDFUL_LAUNCH_WHITE_LIST_KEY)
    val keyMindfulLaunchEnabled = booleanPreferencesKey(Preference.MINDFUL_LAUNCH_ENABLED_KEY)
    override suspend fun addPackageToMLWhiteList(packageName: String) {
        dataStore.edit { prefs ->
            prefs[keyPackageInMLWhiteList] = (prefs[keyPackageInMLWhiteList]?.toMutableSet() ?: emptySet()) + packageName
        }
    }

    override suspend fun removePackageFromMLWhiteList(packageName: String) {
        dataStore.edit { prefs ->
            prefs[keyPackageInMLWhiteList] = (prefs[keyPackageInMLWhiteList]?.toMutableSet()
                ?.minus(packageName)) ?: emptySet()
        }
    }

    override suspend fun isPackageInMLWhiteList(packageName: String): Boolean {
        return dataStore.data.last()[keyPackageInMLWhiteList]?.contains(packageName) ?: false
    }

    override suspend fun getAppsInMLWhiteList(): List<String> {
        return dataStore.data.last()[keyPackageInMLWhiteList]?.toList() ?: emptyList<String>()
    }

    override suspend fun isMindfulLaunchEnabled(): Boolean {
        return dataStore.data.last()[keyMindfulLaunchEnabled] ?: false
    }

    override suspend fun setMindfulLaunchEnabled(isEnabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[keyMindfulLaunchEnabled] = isEnabled
        }
    }
}