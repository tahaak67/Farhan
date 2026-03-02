package ly.com.tahaben.infinite_scroll_blocker_domain.util

interface AccessibilityServiceUtils {
    suspend fun checkIfServiceEnabled(): Boolean
    fun checkIfAccessibilityPermissionGranted(): Boolean
    fun checkIfAppearOnTopPermissionGranted(): Boolean
    fun askForAccessibilityPermission()
    fun askForAppearOnTopPermission()
}