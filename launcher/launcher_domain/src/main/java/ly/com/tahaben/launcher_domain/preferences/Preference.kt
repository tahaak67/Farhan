package ly.com.tahaben.launcher_domain.preferences

import kotlinx.coroutines.flow.Flow

interface Preference {
    fun isLauncherEnabled(): Boolean
    fun setLauncherEnabled(isEnabled: Boolean)
    fun isTimeLimiterEnabled(): Boolean
    fun setTimeLimiterEnabled(isEnabled: Boolean)
    fun addPackageToTimeLimitPackages(packageName: String)
    fun removePackageFromTimeLimitPackages(packageName: String)
    fun isPackageInTimeLimitPackages(packageName: String): Boolean


    suspend fun addPackageToMLWhiteList(packageName: String)
    suspend fun removePackageFromMLWhiteList(packageName: String)
    suspend fun isPackageInMLWhiteList(packageName: String): Boolean
    suspend fun getAppsInMLWhiteList(): List<String>
    suspend fun getAppsInDLWhiteListAsFlow(): Flow<Set<String>>
    suspend fun isDelayedLaunchEnabled(): Flow<Boolean>
    suspend fun setDelayedLaunchEnabled(isEnabled: Boolean)


    companion object {
        const val KEY_LAUNCHER_ENABLED = "key_launcher_enabled"
        const val KEY_TIME_LIMIT_PACKAGES = "key_time_limit_packages"
        const val KEY_TIME_LIMITER_ENABLED = "key_time_limiter_enabled"
        const val MINDFUL_LAUNCH_WHITE_LIST_KEY = "key_mindful_launch_white_list"
        const val MINDFUL_LAUNCH_ENABLED_KEY = "key_mindful_launch_enabled"
    }
}