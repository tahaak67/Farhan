package ly.com.tahaben.notification_filter_presentation.settings

sealed class NotificationSettingsEvent {
    object CancelNotifyMe : NotificationSettingsEvent()
}