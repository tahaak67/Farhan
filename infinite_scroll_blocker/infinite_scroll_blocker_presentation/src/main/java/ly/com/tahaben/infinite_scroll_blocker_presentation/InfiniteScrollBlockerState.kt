package ly.com.tahaben.infinite_scroll_blocker_presentation

import ly.com.tahaben.core.util.UiText

data class InfiniteScrollBlockerState(
    val isServiceEnabled: Boolean = false,
    val isAccessibilityPermissionGranted: Boolean = false,
    val isAppearOnTopPermissionGranted: Boolean = true,
    val timeoutDuration: Int = 3,
    val isCountDownDialogVisible: Boolean = false,
    val countDownSeconds: Int = -1,
    val isMsgDialogVisible: Boolean = false,
    val isMsgDialogInEditMode: Boolean = false,
    val selectedMessage: UiText = UiText.DynamicString(""),
    val msgTextFieldValue: String = "",
    val msgSet: Set<String> = emptySet(),
    val isMsgDropdownExpanded: Boolean = false,
    )
