package ly.com.tahaben.launcher_presentation.wait

import ly.com.tahaben.core.model.AppItem

data class DelayedLaunchState(
    val isMindfulLaunchEnabled: Boolean = false,
    val isAccessibilityPermissionGranted: Boolean = true,
    val isLoading: Boolean = false,
    val delayDurationSeconds: Int = 0,
    val selectedDelayedLaunchMessage: String = "",
    val delayedLaunchMessages: Set<String> = emptySet()

)

data class DelayedLaunchWhiteListState(
    val isMindfulLaunchEnabled: Boolean = false,
    val whiteListedApps: List<String> = emptyList(),
    val searchQuery: String = "",
    val isHintVisible: Boolean = false,
    val isShowSystemApps: Boolean = false,
    val isShowWhiteListOnly: Boolean = false,
    val isAccessibilityPermissionGranted: Boolean = true,
    val isLoading: Boolean = false,
    val appsList: List<AppItem> = emptyList(),
    val searchResults: List<AppItem> = emptyList()
)
