package ly.com.tahaben.notification_filter_presentation

import ly.com.tahaben.notification_filter_domain.model.NotificationItem

data class NotificationFilterState(
    val isServiceEnabled: Boolean = false,
    val isPermissionGranted: Boolean = true,
    val isFirstTimeOpened: Boolean = false,
    val filteredNotifications: List<NotificationItem> = emptyList()
)
