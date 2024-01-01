package ly.com.tahaben.notification_filter_presentation.settings

sealed class UiEventNotificationSettings {
    object NotifyMeEnabled : UiEventNotificationSettings()
    object PerformSilentChecks : UiEventNotificationSettings()
}
