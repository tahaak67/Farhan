package ly.com.tahaben.notification_filter_presentation.settings

import java.time.DayOfWeek

sealed class NotificationSettingsEvent {
    object CancelNotifyMe : NotificationSettingsEvent()
    object ShowNotifyMeTimePicker : NotificationSettingsEvent()
    object DismissNotifyMeTimePicker : NotificationSettingsEvent()
    data class SaveNotifyMeTime(val hour: Int, val min: Int) : NotificationSettingsEvent()
    object DismissPermissionDialog : NotificationSettingsEvent()
    object OpenExactAlarmPermissionPage : NotificationSettingsEvent()
    data class DeclinedPermission(val permission: String) : NotificationSettingsEvent()
    data class DismissWarningDialog(val doNotShowAgain: Boolean) : NotificationSettingsEvent()
    data class OnShouldShowcase(val showcase: Boolean): NotificationSettingsEvent()
    data class SetFilterScheduleEnabled(val isEnabled: Boolean) : NotificationSettingsEvent()
    data class ToggleFilterScheduleDay(val day: DayOfWeek) : NotificationSettingsEvent()
    data class ShowScheduleTimePicker(val target: ScheduleTimePickerTarget) :
        NotificationSettingsEvent()

    object DismissScheduleTimePicker : NotificationSettingsEvent()
    data class SaveScheduleTime(val hour: Int, val min: Int) : NotificationSettingsEvent()
}
