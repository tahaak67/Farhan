package ly.com.tahaben.core.service

interface AccessibilityServiceUtils {
    fun checkIfServiceEnabled(): Boolean
    fun checkIfAccessibilityPermissionGranted(): Boolean
    fun checkIfAppearOnTopPermissionGranted(): Boolean
    fun askForAccessibilityPermission()
    fun askForAppearOnTopPermission()
}