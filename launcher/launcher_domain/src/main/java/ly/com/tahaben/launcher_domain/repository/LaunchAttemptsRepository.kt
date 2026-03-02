package ly.com.tahaben.launcher_domain.repository

import ly.com.tahaben.launcher_domain.model.LaunchAttempt

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 27/3/2025.
 */
interface LaunchAttemptsRepository {
    suspend fun insert(launchAttempt: LaunchAttempt)
    suspend fun getLaunchAttemptsForPackageWithinRange(from: Long, to: Long, packageName: String): Int
    suspend fun getLaunchAttemptsForPackageAfter(from: Long,packageName: String): Int
    suspend fun clearLaunchAttemptsEarlierThan(timestamp: Long): Int

    companion object{
        const val  LAUNCH_ATTEMPTS_CLEANUP = "launch_attempts_cleanup"
    }
}