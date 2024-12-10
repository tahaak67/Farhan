package ly.com.tahaben.launcher_presentation.time_limiter

data class TimeLimiterSettingsState(
    val isTimeLimiterEnabled: Boolean = false,
    val isAppearOnTopPermissionGranted: Boolean = false,
    val isAccessibilityPermissionGranted: Boolean = false
)
