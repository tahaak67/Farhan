package ly.com.tahaben.notification_filter_data.local.preferences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import ly.com.tahaben.core.util.GlobalKey
import ly.com.tahaben.notification_filter_domain.preferences.Preferences
import timber.log.Timber
import java.util.*

class DefaultPreferences(
    private val sharedPref: SharedPreferences
) : Preferences {

    @SuppressLint("ApplySharedPref")
    override fun saveShouldShowOnBoarding(shouldShow: Boolean) {
        //need to use commit to make sure the value is saved because we might read
        // the value again as soon as we finish calling this function
        sharedPref.edit()
            .putBoolean(Preferences.KEY_NOTIFICATION_FILTER_SHOULD_SHOW_ON_BOARDING, shouldShow)
            .commit()
    }

    override fun loadShouldShowOnBoarding(): Boolean {
        return sharedPref.getBoolean(
            Preferences.KEY_NOTIFICATION_FILTER_SHOULD_SHOW_ON_BOARDING,
            true
        )
    }

    override fun loadShouldShowcase(): Boolean {
        return sharedPref.getBoolean(
            Preferences.KEY_NOTIFICATION_FILTER_SHOULD_SHOWCASE,
            true
        )
    }

    override fun saveShouldShowcase(shouldShow: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.KEY_NOTIFICATION_FILTER_SHOULD_SHOWCASE, shouldShow)
            .apply()
    }

    override fun isServiceEnabled(): Boolean {
        return sharedPref.getBoolean(Preferences.KEY_NOTIFICATION_SERVICE_STATS, false) &&
                sharedPref.getBoolean(GlobalKey.Pref_KEY_APP_MAIN_SWITCH, true)
    }

    override fun setServiceState(isEnabled: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.KEY_NOTIFICATION_SERVICE_STATS, isEnabled)
            .apply()
    }

    override fun savePackageToNotificationExceptions(packageName: String) {
        Timber.d("packageName: $packageName")
        val savedSet: MutableSet<String>? =
            sharedPref.getStringSet(
                Preferences.KEY_NOTIFICATION_FILTER_EXCEPTIONS,
                mutableSetOf<String>()
            )
        Timber.d("old set: $savedSet")
        val newSet = savedSet?.toMutableSet()
        newSet?.add(packageName)
        Timber.d("new set: $newSet")
        sharedPref.edit()
            .putStringSet(Preferences.KEY_NOTIFICATION_FILTER_EXCEPTIONS, newSet)
            .apply()
    }

    override fun isPackageInNotificationExceptions(packageName: String): Boolean {
        val set = sharedPref.getStringSet(
            Preferences.KEY_NOTIFICATION_FILTER_EXCEPTIONS,
            emptySet<String>()
        )
        return set?.contains(packageName) == true
    }

    override fun removePackageFromNotificationExceptions(packageName: String) {
        val savedSet: MutableSet<String>? =
            sharedPref.getStringSet(
                Preferences.KEY_NOTIFICATION_FILTER_EXCEPTIONS,
                mutableSetOf<String>()
            )
        Timber.d("old set: $savedSet")
        val newSet = savedSet?.toMutableSet()
        newSet?.remove(packageName)
        sharedPref.edit()
            .putStringSet(Preferences.KEY_NOTIFICATION_FILTER_EXCEPTIONS, newSet)
            .apply()
        Timber.d("new set: $newSet")
    }

    override fun getNotificationFilterExceptionsList(): Set<String> {
        val s: Set<String> = HashSet<String>(
            sharedPref.getStringSet(
                Preferences.KEY_NOTIFICATION_FILTER_EXCEPTIONS,
                emptySet<String>()
            )!!
        )
        return s
    }

    override fun getNotifyMeHours(): Int {
        return sharedPref
            .getInt(Preferences.KEY_NOTIFY_ME_HOUR, -1)
    }

    override fun getNotifyMeMinutes(): Int {

        return sharedPref
            .getInt(Preferences.KEY_NOTIFY_ME_MINUTE, -1)
    }

    override fun setNotifyMeTime(hour: Int, minutes: Int) {
        Timber.d("setting hour: $hour - minutes: $minutes")
        sharedPref.edit()
            .putInt(Preferences.KEY_NOTIFY_ME_HOUR, hour)
            .putInt(Preferences.KEY_NOTIFY_ME_MINUTE, minutes)
            .apply()
    }

    override fun setNotifyMeScheduledDate(date: Long) {
        sharedPref.edit()
            .putLong(Preferences.KEY_NOTIFY_ME_SCHEDULE_DATE, date)
            .apply()
    }

    override fun isNotifyMeScheduledToday(): Boolean {
        val scheduleDate = Calendar.getInstance()
        scheduleDate.timeInMillis = sharedPref.getLong(Preferences.KEY_NOTIFY_ME_SCHEDULE_DATE, -1)
        val calendar = Calendar.getInstance()
        Timber.d("today = ${calendar.time} \nschedule= ${scheduleDate.time}")
        return calendar.get(Calendar.DAY_OF_MONTH) == scheduleDate.get(Calendar.DAY_OF_MONTH) &&
                calendar.get(Calendar.MONTH) == scheduleDate.get(Calendar.MONTH) &&
                calendar.get(Calendar.YEAR) == scheduleDate.get(Calendar.YEAR)
    }
}