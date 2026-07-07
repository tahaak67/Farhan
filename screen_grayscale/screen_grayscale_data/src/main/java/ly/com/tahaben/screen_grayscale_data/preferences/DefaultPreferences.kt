@file:Suppress("UnnecessaryVariable")

package ly.com.tahaben.screen_grayscale_data.preferences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ly.com.tahaben.core.util.GlobalKey
import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences
import timber.log.Timber

class DefaultPreferences(
    private val sharedPref: SharedPreferences,
    private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>
) : Preferences {
    private val mainSwitchKey = booleanPreferencesKey(GlobalKey.Pref_KEY_APP_MAIN_SWITCH)

    @SuppressLint("ApplySharedPref")
    override fun saveShouldShowOnBoarding(shouldShow: Boolean) {
        //need to use commit to make sure the value is saved because we might read
        // the value again as soon as we finish calling this function
        sharedPref.edit()
            .putBoolean(Preferences.KEY_GRAYSCALE_SHOULD_SHOW_ON_BOARDING, shouldShow)
            .commit()
    }

    override fun loadShouldShowOnBoarding(): Boolean {
        return sharedPref.getBoolean(Preferences.KEY_GRAYSCALE_SHOULD_SHOW_ON_BOARDING, true)
    }

    override suspend fun isGrayscaleEnabled(): Boolean {
        return sharedPref.getBoolean(Preferences.KEY_GRAYSCALE_SERVICE_STATS, false) &&
                dataStore.data.map { data -> data[mainSwitchKey] ?:  true }.first()
    }

    override fun setServiceState(isEnabled: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.KEY_GRAYSCALE_SERVICE_STATS, isEnabled)
            .apply()
    }

    override fun savePackageToInfiniteScrollExceptions(packageName: String) {
        Timber.d("packageName: $packageName")
        val savedSet: MutableSet<String>? =
            sharedPref.getStringSet(
                Preferences.KEY_GRAYSCALE_WHITE_LIST,
                mutableSetOf<String>()
            )
        Timber.d("old set: $savedSet")
        val newSet = savedSet?.toMutableSet()
        newSet?.add(packageName)
        Timber.d("new set: $newSet")
        sharedPref.edit()
            .putStringSet(Preferences.KEY_GRAYSCALE_WHITE_LIST, newSet)
            .apply()
    }

    override fun removePackageFromInInfiniteScrollExceptions(packageName: String) {
        val savedSet: MutableSet<String>? =
            sharedPref.getStringSet(
                Preferences.KEY_GRAYSCALE_WHITE_LIST,
                mutableSetOf<String>()
            )
        Timber.d("old set: $savedSet")
        val newSet = savedSet?.toMutableSet()
        newSet?.remove(packageName)
        sharedPref.edit()
            .putStringSet(Preferences.KEY_GRAYSCALE_WHITE_LIST, newSet)
            .apply()
        Timber.d("new set: $newSet")
    }

    override fun isPackageInInfiniteScrollExceptions(packageName: String): Boolean {
        val set = sharedPref.getStringSet(
            Preferences.KEY_GRAYSCALE_WHITE_LIST,
            emptySet<String>()
        )
        return set?.contains(packageName) == true
    }

    override fun getInInfiniteScrollExceptionsList(): Set<String> {
        val s: Set<String> = HashSet<String>(
            sharedPref.getStringSet(
                Preferences.KEY_GRAYSCALE_WHITE_LIST,
                emptySet<String>()
            )!!
        )
        return s
    }

    override fun savePackageToGrayscaleAgnosticList(packageName: String) {
        addPackageToSet(Preferences.KEY_GRAYSCALE_AGNOSTIC_LIST, packageName)
    }

    override fun removePackageFromGrayscaleAgnosticList(packageName: String) {
        removePackageFromSet(Preferences.KEY_GRAYSCALE_AGNOSTIC_LIST, packageName)
    }

    override fun isPackageInGrayscaleAgnosticList(packageName: String): Boolean {
        return sharedPref.getStringSet(Preferences.KEY_GRAYSCALE_AGNOSTIC_LIST, emptySet())
            ?.contains(packageName) == true
    }

    override fun savePackageToGrayscaleColoredList(packageName: String) {
        addPackageToSet(Preferences.KEY_GRAYSCALE_COLORED_LIST, packageName)
    }

    override fun removePackageFromGrayscaleColoredList(packageName: String) {
        removePackageFromSet(Preferences.KEY_GRAYSCALE_COLORED_LIST, packageName)
    }

    override fun isPackageInGrayscaleColoredList(packageName: String): Boolean {
        return sharedPref.getStringSet(Preferences.KEY_GRAYSCALE_COLORED_LIST, emptySet())
            ?.contains(packageName) == true
    }

    private fun addPackageToSet(key: String, packageName: String) {
        val newSet = sharedPref.getStringSet(key, emptySet())
            ?.toMutableSet() ?: mutableSetOf()
        newSet.add(packageName)
        Timber.d("new $key set: $newSet")
        sharedPref.edit()
            .putStringSet(key, newSet)
            .apply()
    }

    private fun removePackageFromSet(key: String, packageName: String) {
        val newSet = sharedPref.getStringSet(key, emptySet())
            ?.toMutableSet() ?: mutableSetOf()
        newSet.remove(packageName)
        Timber.d("new $key set: $newSet")
        sharedPref.edit()
            .putStringSet(key, newSet)
            .apply()
    }
}