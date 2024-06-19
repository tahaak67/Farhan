package ly.com.tahaben.notification_filter_presentation

import ly.com.tahaben.notification_filter_domain.model.NotificationItem

sealed class NotificationFilterEvent {
    data class OnOpenNotification(val notificationItem: NotificationItem) :
        NotificationFilterEvent()

    data class OnDismissNotification(val notificationItem: NotificationItem) :
        NotificationFilterEvent()

    object OnDeleteAllNotifications : NotificationFilterEvent()
    data class OnExcludeAppClick(val appPackageName: String) : NotificationFilterEvent()
    data class OnLaunchAppInfoClick(val appPackageName: String):NotificationFilterEvent()
}
