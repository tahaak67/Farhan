package ly.com.tahaben.infinite_scroll_blocker_presentation

data class NotificationFilterSettingsState(
    val isServiceEnabled: Boolean = false,
    val isNotifyMeEnabled: Boolean = false,
    val notifyMeHour: Int = -1,
    val notifyMeMinute: Int = -1
)
