@file:Suppress("UnnecessaryVariable")

package ly.com.tahaben.infinite_scroll_blocker_data.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ly.com.tahaben.core.util.GlobalKey
import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences
import timber.log.Timber

class DefaultPreferences(
    private val sharedPref: SharedPreferences,
    private val context: Context,
    private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>
) : Preferences {

    private val mainSwitchKey = booleanPreferencesKey(GlobalKey.Pref_KEY_APP_MAIN_SWITCH)

    private val defaultMessages by lazy {
        context.resources.getStringArray(ly.com.tahaben.core.R.array.time_up_messages).toSet()
    }
    override fun loadShouldShowOnBoarding(): Boolean {
        return sharedPref.getBoolean(Preferences.KEY_INFINITE_SCROLL_SHOULD_SHOW_ON_BOARDING, true)
    }

    @SuppressLint("ApplySharedPref")
    override fun saveShouldShowOnBoarding(shouldShow: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.KEY_INFINITE_SCROLL_SHOULD_SHOW_ON_BOARDING, shouldShow)
            .commit()
    }

    override suspend fun isServiceEnabled(): Boolean {
        return sharedPref.getBoolean(Preferences.KEY_INFINITE_SCROLL_SERVICE_STATS, false) &&
                dataStore.data.map { data -> data[mainSwitchKey] ?: true }.first()
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

    /** @return The count down in seconds or -1 if count down is off*/
    override fun getCountDownSeconds(): Int {
        return sharedPref.getInt(Preferences.KEY_COUNT_DOWN_TIME, -1)
    }

    override fun setCountDownSeconds(seconds: Int) {
        sharedPref.edit()
            .putInt(Preferences.KEY_COUNT_DOWN_TIME, seconds)
            .apply()
    }

    override fun setCustomMessage(message: String) {
        sharedPref.edit()
            .putString(Preferences.KEY_CUSTOM_MESSAGE, message)
            .apply()
    }

    override fun getCustomMessage(): String {
        return sharedPref.getString(Preferences.KEY_CUSTOM_MESSAGE, "") ?: ""
    }

    override fun getRandomMessage(): String {
        return sharedPref.getStringSet(Preferences.KEY_MESSAGES_ARRAY, defaultMessages)?.random() ?: throw NullPointerException("empty set")
    }

    override fun addMessageToArray(msg: String) {
        val oldSet: MutableSet<String> =
            sharedPref.getStringSet(
                Preferences.KEY_MESSAGES_ARRAY,
                defaultMessages
            )!!
        val newSet = oldSet.toMutableSet()
        newSet.add(msg)
        sharedPref.edit()
            .putStringSet(Preferences.KEY_MESSAGES_ARRAY, newSet)
            .apply()
    }

    override fun removeMessageFromArray(msg: String) {
        val oldSet: MutableSet<String> =
            sharedPref.getStringSet(
                Preferences.KEY_MESSAGES_ARRAY,
                defaultMessages
            )!!
        val newSet = oldSet.toMutableSet()
        newSet.remove(msg)
        sharedPref.edit()
            .putStringSet(Preferences.KEY_MESSAGES_ARRAY, newSet)
            .apply()
    }
    override fun getMessagesArray(): Set<String> {
        return sharedPref.getStringSet(Preferences.KEY_MESSAGES_ARRAY, defaultMessages)!!
    }

    override fun resetMessagesArray() {
        sharedPref.edit()
            .remove(Preferences.KEY_MESSAGES_ARRAY)
            .apply()
    }
}