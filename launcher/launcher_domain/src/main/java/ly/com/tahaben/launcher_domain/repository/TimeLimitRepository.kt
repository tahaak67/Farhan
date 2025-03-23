package ly.com.tahaben.launcher_domain.repository

import ly.com.tahaben.launcher_domain.model.TimeLimit

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 16,Feb,2023
 */
interface TimeLimitRepository {


    suspend fun addAppTimeLimit(timeLimit: TimeLimit)
    suspend fun updateAppTimeLimit(timeLimit: TimeLimit)
    suspend fun deleteAppTimeLimit(timeLimit: TimeLimit)
    suspend fun getTimeLimitForPackage(packageName: String): TimeLimit?
    fun checkIfAccessibilityPermissionGranted(): Boolean
    fun checkIfAppearOnTopPermissionGranted(): Boolean
    fun askForAccessibilityPermission()
    fun askForAppearOnTopPermission()
    fun launchService()
    fun stopService()

}