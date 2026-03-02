package ly.com.tahaben.launcher_data.local.db

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ly.com.tahaben.launcher_domain.repository.LaunchAttemptsRepository
import kotlin.time.Duration.Companion.hours

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 27/3/2025.
 */
class LaunchAttemptCleanupWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val launchAttemptsRepository: LaunchAttemptsRepository
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val before24Hours = System.currentTimeMillis() - 24.hours.inWholeMilliseconds
            launchAttemptsRepository.clearLaunchAttemptsEarlierThan(before24Hours)
            Result.success()
        }catch (e: Exception){

            Result.failure()
        }
    }
}