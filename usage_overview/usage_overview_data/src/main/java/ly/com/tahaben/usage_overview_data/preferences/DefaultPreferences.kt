package ly.com.tahaben.usage_overview_data.preferences

import android.content.SharedPreferences
import ly.com.tahaben.usage_overview_domain.preferences.Preferences
import ly.com.tahaben.usage_overview_domain.util.WorkerKeys

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 03,May,2023
 */
class DefaultPreferences(private val sharedPref: SharedPreferences) : Preferences {

    override fun isAutoCacheEnabled(): Boolean {
        return sharedPref.getBoolean(Preferences.AUTO_CACHE_KEY, false)
    }

    override fun setAutoCacheEnabled(isEnabled: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.AUTO_CACHE_KEY, isEnabled)
            .apply()
    }

    override fun isCacheEnabled(): Boolean {
        return sharedPref.getBoolean(Preferences.CACHE_KEY, true)
    }

    override fun setCacheEnabled(isEnabled: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.CACHE_KEY, isEnabled)
            .apply()
    }

    override fun getEnabledUsageReports(): Map<String, Boolean> {
        val weekly = sharedPref.getBoolean(WorkerKeys.WEEKLY_USAGE_REPORTS, false)
        val monthly = sharedPref.getBoolean(WorkerKeys.MONTHLY_USAGE_REPORTS, false)
        val yearly = sharedPref.getBoolean(WorkerKeys.YEARLY_USAGE_REPORTS, false)
        return mapOf(
            WorkerKeys.WEEKLY_USAGE_REPORTS to weekly,
            WorkerKeys.MONTHLY_USAGE_REPORTS to monthly,
            WorkerKeys.YEARLY_USAGE_REPORTS to yearly
        )
    }

    override fun setUsageReportsEnabled(usageReports: Map<String, Boolean>) {
        usageReports.forEach { (key, isEnabled) ->
            sharedPref.edit()
                .putBoolean(key, isEnabled)
                .apply()
        }
    }

    override fun getTodayUsage(): Long {
        return sharedPref.getLong(Preferences.TODAY_USAGE, 0)
    }

    override fun setTodayUsage(usage: Long) {
        sharedPref.edit()
            .putLong(Preferences.TODAY_USAGE, usage)
            .apply()
    }

    override fun getYesterdayUsage(): Long {
        return sharedPref.getLong(Preferences.YESTERDAY_USAGE, 0)
    }

    override fun setYesterdayUsage(usage: Long) {
        sharedPref.edit()
            .putLong(Preferences.YESTERDAY_USAGE, usage)
            .apply()
    }

    override fun isIgnoreLauncher(): Boolean {
        return sharedPref.getBoolean(Preferences.IGNORE_LAUNCHER_KEY, true)
    }

    override fun setIgnoreLauncher(isEnabled: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.IGNORE_LAUNCHER_KEY, isEnabled)
            .apply()
    }

    override fun isIgnoreFarhan(): Boolean {
        return sharedPref.getBoolean(Preferences.IGNORE_FARHAN_KEY, false)
    }

    override fun setIgnoreFarhan(isEnabled: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.IGNORE_FARHAN_KEY, isEnabled)
            .apply()
    }
}