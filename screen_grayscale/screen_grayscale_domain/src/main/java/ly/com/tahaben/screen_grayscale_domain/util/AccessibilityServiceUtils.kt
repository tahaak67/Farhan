package ly.com.tahaben.screen_grayscale_domain.util

interface AccessibilityServiceUtils {
    suspend fun checkIfServiceEnabled(): Boolean
    fun checkIfAccessibilityPermissionGranted(): Boolean
    fun checkIfAppearOnTopPermissionGranted(): Boolean
    fun askForAccessibilityPermission()
    fun askForAppearOnTopPermission()
}