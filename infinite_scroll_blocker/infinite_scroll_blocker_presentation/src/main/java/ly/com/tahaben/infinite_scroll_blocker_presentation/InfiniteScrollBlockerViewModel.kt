package ly.com.tahaben.infinite_scroll_blocker_presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.InfiniteScrollUseCases
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfiniteScrollBlockerViewModel @Inject constructor(
    private val infiniteScrollUseCases: InfiniteScrollUseCases,
    private val preferences: Preferences
) : ViewModel() {

    var state by mutableStateOf(InfiniteScrollBlockerState())
        private set

    init {
        checkServiceStats()
        getTimeoutDuration()
        loadCountDownTime()
        getMsg()
        getMessages()
    }

    fun checkServiceStats() {
        checkAccessibilityPermissionStats()
        state = state.copy(
            isServiceEnabled = infiniteScrollUseCases.isServiceEnabled() && infiniteScrollUseCases.isAccessibilityPermissionGranted()
        )
    }

    private fun checkAccessibilityPermissionStats() {
        state = state.copy(
            isAccessibilityPermissionGranted = infiniteScrollUseCases.isAccessibilityPermissionGranted()
        )
        Timber.d("state = ${state.isAccessibilityPermissionGranted}")
    }

    fun setServiceStats(isEnabled: Boolean) {
        infiniteScrollUseCases.setServiceState(isEnabled)
        if (!state.isAccessibilityPermissionGranted) {
            infiniteScrollUseCases.askForAccessibilityPermission()
        }
        state = state.copy(
            isServiceEnabled = isEnabled
        )
    }

    fun setTimeoutDuration(minutes: Int) {
        infiniteScrollUseCases.setTimeOutDuration(minutes)
        state = state.copy(
            timeoutDuration = minutes
        )
    }

    private fun getTimeoutDuration() {
        state = state.copy(
            timeoutDuration = infiniteScrollUseCases.getTimeOutDuration()
        )
    }

    fun checkIfAppearOnTopPermissionGranted() {
        state = state.copy(
            isAppearOnTopPermissionGranted = infiniteScrollUseCases.isAppearOnTopPermissionGranted()
        )
    }

    fun askForAppearOnTopPermission() {
        infiniteScrollUseCases.askForAppearOnTopPermission()
    }

    fun askForAccessibilityPermission() {
        infiniteScrollUseCases.askForAccessibilityPermission()
    }

    fun onEvent(event: InfiniteScrollEvent){
        Timber.d("event: $event")
        when (event) {
            is InfiniteScrollEvent.OnCountDownDialogVisible -> {
                state = state.copy(
                    isCountDownDialogVisible = event.isVisible
                )
            }
            is InfiniteScrollEvent.SaveCountDownTime -> {
                Timber.d("saveCountDownTime: ${event.seconds}")
                saveCountDownTime(event.seconds)
            }

            is InfiniteScrollEvent.OnMsgDialogVisible -> {
                state = state.copy(
                    isMsgDialogVisible = event.isVisible
                )
            }
            is InfiniteScrollEvent.SaveMsg -> {
                selectMsg(event.msg)
            }

            is InfiniteScrollEvent.OnMsgTextFieldValueChange -> {
                state = state.copy(
                    msgTextFieldValue = event.text
                )
            }
            is InfiniteScrollEvent.OnAddMsg -> {
                addMsg(event.msg)
                state = state.copy(
                    msgTextFieldValue = ""
                )
            }
            is InfiniteScrollEvent.OnDeleteMsg -> {
                deleteMsg(event.msg)
            }
            is InfiniteScrollEvent.OnMsgDropDownExpanded -> {
                state = state.copy(
                    isMsgDropdownExpanded = event.isExpanded
                )
            }
            is InfiniteScrollEvent.OnMsgSelected -> {
                selectMsg(event.msg)
                state = state.copy(
                    isMsgDropdownExpanded = false
                )
            }
            InfiniteScrollEvent.ResetMessages -> {
                resetMessages()
            }

            InfiniteScrollEvent.OnSwitchMsgDialogMode -> {

                state = state.copy(
                    isMsgDialogInEditMode = !state.isMsgDialogInEditMode
                )
            }
        }
    }

    private fun saveCountDownTime(seconds: Int){
        preferences.setCountDownSeconds(seconds)
        state = state.copy(
            countDownSeconds = seconds,
            isCountDownDialogVisible = false
        )
    }

    private fun loadCountDownTime(){
        val countdown = infiniteScrollUseCases.getCountDown()
        state = state.copy(
            countDownSeconds = countdown
        )
    }

    private fun selectMsg(msg: String){
        preferences.setCustomMessage(msg)
        state = state.copy(
            selectedMessage = if (msg.isBlank()) UiText.StringResource(ly.com.tahaben.core.R.string.msg_random) else UiText.DynamicString(msg)
        )
    }

    private fun getMsg(){
        val msg = preferences.getCustomMessage()
        state = state.copy(
            selectedMessage = if (msg.isBlank()) UiText.StringResource(ly.com.tahaben.core.R.string.msg_random) else UiText.DynamicString(msg)
        )
    }

    private fun getMessages(){
        state = state.copy(
            msgSet = preferences.getMessagesArray()
        )
    }

    private fun resetMessages(){
        preferences.resetMessagesArray()
        getMessages()
    }

    private fun addMsg(msg: String){
        preferences.addMessageToArray(msg)
        getMessages()
    }

    private fun deleteMsg(msg: String){
        preferences.removeMessageFromArray(msg)
        getMessages()
    }


}