package ly.com.tahaben.notification_filter_presentation.settings

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.time.DayOfWeek
import java.time.LocalTime

data class NotificationFilterSettingsState(
    val isServiceEnabled: Boolean = false,
    val isNotifyMeEnabled: Boolean = false,
    val notifyMeHour: Int = -1,
    val notifyMeMinute: Int = -1,
    val isTimePickerVisible: Boolean = false,
    val visiblePermissionDialogQueue: SnapshotStateList<String> = mutableStateListOf(),
    val declinedPermissions: SnapshotStateList<String> = mutableStateListOf(),
    val isWarningDialogVisible: Boolean = false,
    val isShowcaseOn: Boolean = false,
    val isFilterScheduleEnabled: Boolean = false,
    val filterScheduleDays: Set<DayOfWeek> = emptySet(),
    val filterScheduleStartTime: LocalTime = LocalTime.MIDNIGHT,
    val filterScheduleEndTime: LocalTime = LocalTime.MIDNIGHT,
    val scheduleTimePickerTarget: ScheduleTimePickerTarget? = null
)

enum class ScheduleTimePickerTarget {
    START, END
}
