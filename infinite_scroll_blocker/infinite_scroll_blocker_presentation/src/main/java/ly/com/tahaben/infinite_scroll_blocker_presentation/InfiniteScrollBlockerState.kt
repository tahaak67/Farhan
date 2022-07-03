package ly.com.tahaben.infinite_scroll_blocker_presentation

data class InfiniteScrollBlockerState(
    val isServiceEnabled: Boolean = false,
    val isAccessibilityPermissionGranted: Boolean = false,
    val isAppearOnTopPermissionGranted: Boolean = true,
    val timeoutDuration: Int = 3
)
