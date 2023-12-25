@file:Suppress("UnnecessaryVariable")

package ly.com.tahaben.screen_grayscale_data.preferences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import ly.com.tahaben.core.util.GlobalKey
import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences
import timber.log.Timber

class DefaultPreferences(
    private val sharedPref: SharedPreferences
) : Preferences {

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

    override fun isGrayscaleEnabled(): Boolean {
        return sharedPref.getBoolean(Preferences.KEY_GRAYSCALE_SERVICE_STATS, false) &&
                sharedPref.getBoolean(GlobalKey.Pref_KEY_APP_MAIN_SWITCH, true)
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
}