package ly.com.tahaben.usage_overview_domain.preferences

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 03,May,2023
 */
interface Preferences {
    fun isAutoCacheEnabled(): Boolean
    fun setAutoCacheEnabled(isEnabled: Boolean)
    fun isCacheEnabled(): Boolean
    fun setCacheEnabled(isEnabled: Boolean)
    fun getEnabledUsageReports(): Map<String, Boolean>
    fun setUsageReportsEnabled(usageReports: Map<String, Boolean>)
    fun getTodayUsage(): Long
    fun setTodayUsage(usage: Long)
    fun getYesterdayUsage(): Long
    fun setYesterdayUsage(usage: Long)
    fun isIgnoreLauncher(): Boolean
    fun setIgnoreLauncher(isEnabled: Boolean)
    fun isIgnoreFarhan(): Boolean
    fun setIgnoreFarhan(isEnabled: Boolean)



    companion object {
        const val AUTO_CACHE_KEY = "auto_cache_key"
        const val CACHE_KEY = "cache_key"
        const val TODAY_USAGE = "today_usage_key"
        const val YESTERDAY_USAGE = "yesterday_usage_key"
        const val IGNORE_LAUNCHER_KEY = "ignore_launcher_key"
        const val IGNORE_FARHAN_KEY = "ignore_farhan_key"
    }
}