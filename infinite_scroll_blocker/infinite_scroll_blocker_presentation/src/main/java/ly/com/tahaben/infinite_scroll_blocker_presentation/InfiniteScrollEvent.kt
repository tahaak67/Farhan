package ly.com.tahaben.infinite_scroll_blocker_presentation

sealed class InfiniteScrollEvent {
    data class OnCountDownDialogVisible(val isVisible: Boolean): InfiniteScrollEvent()
    data class SaveCountDownTime(val seconds: Int): InfiniteScrollEvent()
    data class OnMsgDialogVisible(val isVisible: Boolean): InfiniteScrollEvent()
    data class SaveMsg(val msg: String): InfiniteScrollEvent()
    data class OnMsgTextFieldValueChange(val text: String): InfiniteScrollEvent()
    object ResetMessages: InfiniteScrollEvent()
    data class OnMsgSelected(val msg: String): InfiniteScrollEvent()
    data class OnDeleteMsg(val msg: String): InfiniteScrollEvent()
    data class OnAddMsg(val msg: String): InfiniteScrollEvent()
    data class OnMsgDropDownExpanded(val isExpanded: Boolean): InfiniteScrollEvent()
    object OnSwitchMsgDialogMode: InfiniteScrollEvent()
}