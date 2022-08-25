package ly.com.tahaben.launcher_presentation

import ly.com.tahaben.core.model.AppItem

data class LauncherState(
    val isLoading: Boolean = true,
    val appsList: List<AppItem> = emptyList(),
    val searchResults: List<AppItem> = emptyList(),
    val query: String = "",
    val isHintVisible: Boolean = true
)
