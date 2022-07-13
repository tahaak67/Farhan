package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.repository.UsageRepository

class IsUsagePermissionGranted(
    private val usageRepository: UsageRepository
) {
    operator fun invoke(): Boolean {
        return usageRepository.checkUsagePermission()
    }
}
