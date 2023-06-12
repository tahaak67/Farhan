package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.repository.WorkerRepository

class SetUsageReportsEnabled(private val workerRepository: WorkerRepository) {
    operator fun invoke(usageReports: Map<String, Boolean>) {
        workerRepository.switchReportsEnabled(usageReports)
    }
}
