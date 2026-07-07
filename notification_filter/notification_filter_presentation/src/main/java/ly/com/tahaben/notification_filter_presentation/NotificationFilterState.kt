package ly.com.tahaben.notification_filter_presentation

import ly.com.tahaben.notification_filter_domain.model.NotificationItem

data class NotificationFilterState(
    val isServiceEnabled: Boolean = false,
    val isPermissionGranted: Boolean = true,
    val isFirstTimeOpened: Boolean = false,
    val isSearchActive: Boolean = false,
    val searchQuery: String = "",
    // The list actually shown on screen: the full DB list when not searching,
    // or the in-memory search result when a query is present.
    val filteredNotifications: List<NotificationItem> = emptyList()
)
