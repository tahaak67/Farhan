package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.repository.WorkerRepository

class OpenBatteryOptimizationSettings(private val workerRepository: WorkerRepository) {
    operator fun invoke() {
        workerRepository.requestToIgnoreBatteryOptimization()
    }
}
