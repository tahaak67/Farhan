package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.repository.WorkerRepository

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 30,Apr,2023
 */
class SetAutoCachingEnabled(private val repository: WorkerRepository) {
    operator fun invoke(isEnabled: Boolean) {
        repository.switchAutoCacheEnabled(isEnabled)
    }
}