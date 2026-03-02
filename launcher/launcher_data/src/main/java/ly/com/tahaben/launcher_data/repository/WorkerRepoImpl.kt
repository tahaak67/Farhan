package ly.com.tahaben.launcher_data.repository

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ly.com.tahaben.launcher_data.local.db.LaunchAttemptCleanupWorker
import ly.com.tahaben.launcher_domain.repository.LaunchAttemptsRepository
import ly.com.tahaben.launcher_domain.repository.WorkerRepository
import timber.log.Timber
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 28/3/2025.
 */
class WorkerRepoImpl(
    private val workManager: WorkManager
): WorkerRepository {
    override fun scheduleLaunchAttemptCleanupWork() {
        Timber.d("scheduleWorkManager for launch attempt cleanup")
        val cleanupWorker = PeriodicWorkRequestBuilder<LaunchAttemptCleanupWorker>(24.hours.toJavaDuration())
            .build()
        workManager.enqueueUniquePeriodicWork(
            LaunchAttemptsRepository.LAUNCH_ATTEMPTS_CLEANUP,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            cleanupWorker
        )
    }

    override fun cancelLaunchAttemptCleanupWork() {
        Timber.d("cancelWorkManager for launch attempt cleanup")
        workManager.cancelUniqueWork(LaunchAttemptsRepository.LAUNCH_ATTEMPTS_CLEANUP)
    }
}