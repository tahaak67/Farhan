package ly.com.tahaben.launcher_domain.use_case.time_limit

import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.core.model.AppItem

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 18,Feb,2023
 */
class GetInstalledApps(private val installedAppsRepo: InstalledAppsRepository) {
    suspend operator fun invoke(): List<AppItem> {
        return installedAppsRepo.getInstalledApps()
    }
}