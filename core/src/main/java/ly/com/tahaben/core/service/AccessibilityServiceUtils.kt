package ly.com.tahaben.core.service

interface AccessibilityServiceUtils {
    suspend fun checkIfServiceEnabled(): Boolean
    fun checkIfAccessibilityPermissionGranted(): Boolean
    fun checkIfAppearOnTopPermissionGranted(): Boolean
    fun askForAccessibilityPermission()
    fun askForAppearOnTopPermission()
}