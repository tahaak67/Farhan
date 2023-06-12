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

    companion object {
        const val AUTO_CACHE_KEY = "auto_cache_key"
        const val CACHE_KEY = "cache_key"
    }
}