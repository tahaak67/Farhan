package ly.com.tahaben.screen_grayscale_presentation.exceptions

import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.screen_grayscale_domain.model.GrayscaleAppState

data class GrayscaleWhiteListState(
    val isLoading: Boolean = true,
    val installedApps: List<GrayscaleApp> = emptyList(),
    val searchResults: List<GrayscaleApp> = emptyList(),
    val showSystemApps: Boolean = false,
    val query: String = "",
    val isHintVisible: Boolean = false,
    val isSearchFieldVisible: Boolean = false,
    val showWhitelistOnly: Boolean = false
)

data class GrayscaleApp(
    val app: AppItem,
    val grayscaleState: GrayscaleAppState
)
