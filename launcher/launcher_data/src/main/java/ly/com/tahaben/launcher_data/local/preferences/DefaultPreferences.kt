package ly.com.tahaben.launcher_data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.GlobalKey
import ly.com.tahaben.launcher_data.local.Constants
import ly.com.tahaben.launcher_domain.preferences.Preference
import timber.log.Timber

class DefaultPreferences(
    private val sharedPref: SharedPreferences,
    private val dataStore: DataStore<Preferences>,
    private val context: Context
) : Preference {

    private val mainSwitchKey = booleanPreferencesKey(GlobalKey.Pref_KEY_APP_MAIN_SWITCH)
    private val delayedLaunchDurationKey = intPreferencesKey(Preference.DELAYED_LAUNCH_DURATION_KEY)
    private val delayedMessageKey = stringSetPreferencesKey(Preference.DELAYED_LAUNCH_MESSAGES_KEY)
    private val selectedDelayedMessageKey = stringPreferencesKey(Preference.SELECTED_DELAYED_LAUNCH_MESSAGES_KEY)

    private val defaultMessages by lazy {
        context.resources.getStringArray(R.array.delayed_launch_messages)
    }

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

    override suspend fun isPackageInDelayedLaunchWhiteList(packageName: String): Boolean {
        return dataStore.data.catch {
            it.printStackTrace()
        }.firstOrNull()?.get(keyPackageInMLWhiteList)?.contains(packageName) ?: false
    }

    override suspend fun getAppsInDelayedLaunchWhiteList(): List<String> {
        return dataStore.data.catch {
            it.printStackTrace()
        }.firstOrNull()?.get(keyPackageInMLWhiteList)?.toList() ?: emptyList<String>()
    }

    override suspend fun getAppsInDLWhiteListAsFlow(): Flow<Set<String>> {
        return dataStore.data.catch {
            it.printStackTrace()
        }.map {
            it[keyPackageInMLWhiteList] ?: emptySet()
        }
    }

    override suspend fun isDelayedLaunchEnabled(): Flow<Boolean> {
        return dataStore.data.map {
            it[keyMindfulLaunchEnabled] ?: false &&
            it[mainSwitchKey] ?: true
        }
    }

    override suspend fun setDelayedLaunchEnabled(isEnabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[keyMindfulLaunchEnabled] = isEnabled
        }
    }

    override suspend fun setDelayedLaunchDuration(seconds: Int) {
        dataStore.edit { prefs ->
            prefs[delayedLaunchDurationKey] = seconds
        }
    }

    override suspend fun getDelayedLaunchDuration(): Flow<Int> {
        return dataStore.data.map { prefs ->
            prefs[delayedLaunchDurationKey] ?: 5
        }
    }

    override suspend fun getDelayedLaunchMessages(): Flow<Set<String>> {
        return dataStore.data.map { prefs ->
            prefs[delayedMessageKey] ?: defaultMessages.toSet()
        }
    }

    override suspend fun getDelayedLaunchMessage(): Flow<String> {
        return dataStore.data.map { prefs ->
            prefs[selectedDelayedMessageKey] ?: ""
        }
    }

    override suspend fun setDelayedLaunchMessage(message: String) {
        dataStore.edit { prefs ->
            prefs[selectedDelayedMessageKey] = message
        }
    }

    override suspend fun addDelayedLaunchMessage(message: String) {
        dataStore.edit { prefs ->
            prefs[delayedMessageKey] = prefs[delayedMessageKey]?.toMutableSet()?.apply { add(message) } ?: defaultMessages.toMutableSet().apply { add(message) }
        }
    }

    override suspend fun removeDelayedLaunchMessage(message: String) {
        dataStore.edit { prefs ->
            prefs[delayedMessageKey] = prefs[delayedMessageKey]?.toMutableSet()?.apply { remove(message) } ?: defaultMessages.toMutableSet().apply { remove(message) }
        }
    }

    override suspend fun resetDelayedLaunchMessages() {
        dataStore.edit { prefs ->
            prefs[delayedMessageKey] = defaultMessages.toSet()
        }
    }
}