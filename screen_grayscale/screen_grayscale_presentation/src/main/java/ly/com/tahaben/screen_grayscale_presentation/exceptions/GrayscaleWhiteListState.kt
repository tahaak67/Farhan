package ly.com.tahaben.screen_grayscale_presentation.exceptions

import ly.com.tahaben.core.model.AppItem

data class GrayscaleWhiteListState(
    val isLoading: Boolean = true,
    val installedApps: List<AppItem> = emptyList(),
    val searchResults: List<AppItem> = emptyList(),
    val showSystemApps: Boolean = false,
    val query: String = "",
    val isHintVisible: Boolean = false,
    val isSearchFieldVisible: Boolean = false,
)
