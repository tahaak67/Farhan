package ly.com.tahaben.notification_filter_presentation.settings.exceptions

import ly.com.tahaben.core.model.AppItem

data class NotificationFilterExceptionsState(
    val isLoading: Boolean = true,
    val appsList: List<AppItem> = emptyList(),
    val searchResults: List<AppItem> = emptyList(),
    val showSystemApps: Boolean = false,
    val query: String = "",
    val isHintVisible: Boolean = false,
    val isSearchFieldVisible: Boolean = false,
    val showExceptionsOnly: Boolean = false
)
