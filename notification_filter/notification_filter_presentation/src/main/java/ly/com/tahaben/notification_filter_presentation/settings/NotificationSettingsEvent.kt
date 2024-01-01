package ly.com.tahaben.notification_filter_presentation.settings

sealed class NotificationSettingsEvent {
    object CancelNotifyMe : NotificationSettingsEvent()
    object ShowNotifyMeTimePicker : NotificationSettingsEvent()
    object DismissNotifyMeTimePicker : NotificationSettingsEvent()
    data class SaveNotifyMeTime(val hour: Int, val min: Int) : NotificationSettingsEvent()
    object DismissPermissionDialog : NotificationSettingsEvent()
    object OpenExactAlarmPermissionPage : NotificationSettingsEvent()
    data class DeclinedPermission(val permission: String) : NotificationSettingsEvent()

}