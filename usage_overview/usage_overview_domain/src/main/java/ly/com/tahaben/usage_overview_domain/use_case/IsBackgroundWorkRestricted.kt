package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.repository.WorkerRepository

class IsBackgroundWorkRestricted(private val workerRepository: WorkerRepository) {
    operator fun invoke(): Boolean {
        return workerRepository.checkIfBackgroundWorkRestricted()
    }
}
