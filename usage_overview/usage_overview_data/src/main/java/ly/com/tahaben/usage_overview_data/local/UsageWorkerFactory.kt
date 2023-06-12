package ly.com.tahaben.usage_overview_data.local

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import ly.com.tahaben.usage_overview_domain.use_case.UsageOverviewUseCases

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 30,Apr,2023
 */
class UsageWorkerFactory constructor(
    private val usageRepository: UsageRepository,
    private val usageOverviewUseCases: UsageOverviewUseCases
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            CacheWorker::class.java.name -> {
                CacheWorker(appContext, workerParameters, usageRepository)
            }

            ReportsWorker::class.java.name -> {
                ReportsWorker(appContext, workerParameters, usageRepository, usageOverviewUseCases)
            }

            else -> null
        }
    }
}
