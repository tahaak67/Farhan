package ly.com.tahaben.launcher_presentation.launcher

import ly.com.tahaben.core.model.AppItem

data class LauncherState(
    val isLoading: Boolean = true,
    val appsList: List<AppItem> = emptyList(),
    val searchResults: List<AppItem> = emptyList(),
    val query: String = "",
    val isHintVisible: Boolean = true,
    val isTimeLimitDialogVisible: Boolean = false,
    val timeLimitedApp: AppItem? = null,
    val isDelayRunning: Boolean = false,
    val isConfirmOpenVisible: Boolean = false
)
