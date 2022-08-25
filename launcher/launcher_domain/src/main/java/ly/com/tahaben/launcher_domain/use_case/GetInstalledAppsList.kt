package ly.com.tahaben.launcher_domain.use_case

import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.core.model.AppItem

class GetInstalledAppsList(
    private val installedAppsRepo: InstalledAppsRepository
) {

    suspend operator fun invoke(): List<AppItem> {
        return installedAppsRepo.getInstalledApps()
    }
}