package ly.com.tahaben.launcher_domain.repository

import ly.com.tahaben.core.model.AppItem

interface AvailableActivitiesRepository {
    fun getActivities(): List<AppItem>
    fun launchMainActivityFor(app: AppItem)
    fun launchAppInfo(app: AppItem)
    fun launchDefaultDialerApp()
    fun launchDefaultCameraApp()
    fun launchDefaultAlarmApp()
}