package ly.com.tahaben.launcher_domain.use_case.time_limit

import ly.com.tahaben.launcher_domain.model.TimeLimit
import ly.com.tahaben.launcher_domain.repository.TimeLimitRepository

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 16,Feb,2023
 */
class AddTimeLimitToDb(private val timeLimitRepo: TimeLimitRepository) {
    suspend operator fun invoke(appTimeLimit: TimeLimit) {
        timeLimitRepo.addAppTimeLimit(appTimeLimit)
    }
}