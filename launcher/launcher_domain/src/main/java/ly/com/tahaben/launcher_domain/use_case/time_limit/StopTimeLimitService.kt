package ly.com.tahaben.launcher_domain.use_case.time_limit

import ly.com.tahaben.launcher_domain.repository.TimeLimitRepository

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 16,Feb,2023
 */
class StopTimeLimitService(private val timeLimitRepository: TimeLimitRepository) {

    operator fun invoke() {
        timeLimitRepository.stopService()
    }
}