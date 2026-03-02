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
    suspend fun isPackageInDelayedLaunchWhiteList(packageName: String): Boolean
    suspend fun getAppsInDelayedLaunchWhiteList(): List<String>
    suspend fun getAppsInDLWhiteListAsFlow(): Flow<Set<String>>
    suspend fun isDelayedLaunchEnabled(): Flow<Boolean>
    suspend fun setDelayedLaunchEnabled(isEnabled: Boolean)
    suspend fun setDelayedLaunchDuration(seconds: Int)
    suspend fun getDelayedLaunchDuration(): Flow<Int>
    suspend fun getDelayedLaunchMessages(): Flow<Set<String>>
    suspend fun getDelayedLaunchMessage(): Flow<String>
    suspend fun setDelayedLaunchMessage(message: String)
    suspend fun addDelayedLaunchMessage(message: String)
    suspend fun removeDelayedLaunchMessage(message: String)
    suspend fun resetDelayedLaunchMessages()


    companion object {
        const val KEY_LAUNCHER_ENABLED = "key_launcher_enabled"
        const val KEY_TIME_LIMIT_PACKAGES = "key_time_limit_packages"
        const val KEY_TIME_LIMITER_ENABLED = "key_time_limiter_enabled"
        const val MINDFUL_LAUNCH_WHITE_LIST_KEY = "key_mindful_launch_white_list"
        const val MINDFUL_LAUNCH_ENABLED_KEY = "key_mindful_launch_enabled"
        const val DELAYED_LAUNCH_DURATION_KEY = "key_delayed_launch_duration"
        const val DELAYED_LAUNCH_MESSAGES_KEY = "key_delayed_launch_messages"
        const val SELECTED_DELAYED_LAUNCH_MESSAGES_KEY = "key_selected_delayed_launch_messages"
    }
}