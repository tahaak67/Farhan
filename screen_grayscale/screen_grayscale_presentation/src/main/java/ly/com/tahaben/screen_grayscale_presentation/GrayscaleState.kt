package ly.com.tahaben.screen_grayscale_presentation

data class GrayscaleState(
    val isServiceEnabled: Boolean = false,
    val isDeviceRooted: Boolean = false,
    val isSecureSettingsPermissionGranted: Boolean = false,
    val isAccessibilityPermissionGranted: Boolean = false,
    val isLoading: Boolean = false
)
