package ly.com.tahaben.core.data.repository

import ly.com.tahaben.core.model.AppItem

interface InstalledAppsRepository {

    suspend fun getInstalledApps(): List<AppItem>
}