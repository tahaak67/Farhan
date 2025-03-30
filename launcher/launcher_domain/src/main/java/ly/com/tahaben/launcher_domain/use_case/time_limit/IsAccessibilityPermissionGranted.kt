package ly.com.tahaben.launcher_domain.use_case.time_limit

import ly.com.tahaben.launcher_domain.repository.TimeLimitRepository

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 18,Feb,2023
 */
class IsAccessibilityPermissionGranted(private val timeLimiterRepository: TimeLimitRepository) {
    operator fun invoke(): Boolean {
        return timeLimiterRepository.checkIfAccessibilityPermissionGranted()
    }
}