package ly.com.tahaben.notification_filter_presentation.settings

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class NotificationFilterSettingsState(
    val isServiceEnabled: Boolean = false,
    val isNotifyMeEnabled: Boolean = false,
    val notifyMeHour: Int = -1,
    val notifyMeMinute: Int = -1,
    val isTimePickerVisible: Boolean = false,
    val visiblePermissionDialogQueue: SnapshotStateList<String> = mutableStateListOf(),
    val declinedPermissions: SnapshotStateList<String> = mutableStateListOf(),
    val isWarningDialogVisible: Boolean = false,
    val isShowcaseOn: Boolean = false
)
