package ly.com.tahaben.launcher_domain.repository

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 28/3/2025.
 */
interface WorkerRepository {
    fun scheduleLaunchAttemptCleanupWork()
    fun cancelLaunchAttemptCleanupWork()
}