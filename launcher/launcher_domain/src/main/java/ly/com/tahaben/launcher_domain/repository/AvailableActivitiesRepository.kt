package ly.com.tahaben.launcher_domain.repository

import kotlinx.coroutines.flow.Flow
import ly.com.tahaben.core.model.AppItem

interface AvailableActivitiesRepository {
    suspend fun getActivities(): List<AppItem>

    suspend fun deleteActivity(appItem: AppItem)
    fun loadActivitiesFromDb(): Flow<List<AppItem>>
    fun launchActivity(app: AppItem)
    fun launchAppInfo(app: AppItem)
    fun launchDefaultDialerApp()
    fun launchDefaultCameraApp()
    fun launchDefaultAlarmApp()

}