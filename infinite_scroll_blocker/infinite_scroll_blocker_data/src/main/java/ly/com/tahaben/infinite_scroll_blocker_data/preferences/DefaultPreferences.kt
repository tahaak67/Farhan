@file:Suppress("UnnecessaryVariable")

package ly.com.tahaben.infinite_scroll_blocker_data.preferences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core.util.GlobalKey
import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences
import timber.log.Timber

class DefaultPreferences(
    private val sharedPref: SharedPreferences
) : Preferences {

    override fun loadShouldShowOnBoarding(): Boolean {
        return sharedPref.getBoolean(Preferences.KEY_INFINITE_SCROLL_SHOULD_SHOW_ON_BOARDING, true)
    }

    @SuppressLint("ApplySharedPref")
    override fun saveShouldShowOnBoarding(shouldShow: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.KEY_INFINITE_SCROLL_SHOULD_SHOW_ON_BOARDING, shouldShow)
            .commit()
    }

    override fun isServiceEnabled(): Boolean {
        return sharedPref.getBoolean(Preferences.KEY_INFINITE_SCROLL_SERVICE_STATS, false) &&
                sharedPref.getBoolean(GlobalKey.Pref_KEY_APP_MAIN_SWITCH, true)
    }

    override fun setServiceState(isEnabled: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.KEY_INFINITE_SCROLL_SERVICE_STATS, isEnabled)
            .apply()
    }

    override fun savePackageToInfiniteScrollExceptions(packageName: String) {
        Timber.d("packageName: $packageName")
        val savedSet: MutableSet<String>? =
            sharedPref.getStringSet(
                Preferences.KEY_INFINITE_SCROLL_EXCEPTIONS,
                mutableSetOf<String>()
            )
        Timber.d("old set: $savedSet")
        val newSet = savedSet?.toMutableSet()
        newSet?.add(packageName)
        Timber.d("new set: $newSet")
        sharedPref.edit()
            .putStringSet(Preferences.KEY_INFINITE_SCROLL_EXCEPTIONS, newSet)
            .apply()
    }

    override fun removePackageFromInInfiniteScrollExceptions(packageName: String) {
        val savedSet: MutableSet<String>? =
            sharedPref.getStringSet(
                Preferences.KEY_INFINITE_SCROLL_EXCEPTIONS,
                mutableSetOf<String>()
            )
        Timber.d("old set: $savedSet")
        val newSet = savedSet?.toMutableSet()
        newSet?.remove(packageName)
        sharedPref.edit()
            .putStringSet(Preferences.KEY_INFINITE_SCROLL_EXCEPTIONS, newSet)
            .apply()
        Timber.d("new set: $newSet")
    }

    override fun isPackageInInfiniteScrollExceptions(packageName: String): Boolean {
        val set = sharedPref.getStringSet(
            Preferences.KEY_INFINITE_SCROLL_EXCEPTIONS,
            emptySet<String>()
        )
        return set?.contains(packageName) == true
    }

    override fun getInInfiniteScrollExceptionsList(): Set<String> {
        val s: Set<String> = HashSet<String>(
            sharedPref.getStringSet(
                Preferences.KEY_INFINITE_SCROLL_EXCEPTIONS,
                emptySet<String>()
            )!!
        )
        return s
    }

    override fun getInfiniteScrollTimeOut(): Int {
        return sharedPref
            .getInt(Preferences.KEY_INFINITE_SCROLL_TIME_OUT, 3)
    }

    override fun setInfiniteScrollTimeOut(minutes: Int) {
        Timber.d("setting minutes: $minutes")
        sharedPref.edit()
            .putInt(Preferences.KEY_INFINITE_SCROLL_TIME_OUT, minutes)
            .apply()
    }

    override fun loadDarkModeOn(): String {
        return sharedPref.getString(
            Preferences.KEY_APP_DARK_MODE_ON,
            UIModeAppearance.FOLLOW_SYSTEM.name
        )
            ?: UIModeAppearance.FOLLOW_SYSTEM.name
    }

    override fun loadThemeColors(): String {
        return sharedPref.getString(
            Preferences.KEY_APP_THEME_COLORS,
            "Unknown"
        ) ?: "Unknown"
    }
}