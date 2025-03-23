package ly.com.tahaben.launcher_presentation.time_limiter

import ly.com.tahaben.core.model.AppItem

data class TimeLimiterWhitelistState(
    val isLoading: Boolean = true,
    val installedApps: List<AppItem> = emptyList(),
    val searchResults: List<AppItem> = emptyList(),
    val showSystemApps: Boolean = false,
    val query: String = "",
    val isHintVisible: Boolean = false,
    val isSearchFieldVisible: Boolean = false,
    val isExceptionsOnly: Boolean = false
)
