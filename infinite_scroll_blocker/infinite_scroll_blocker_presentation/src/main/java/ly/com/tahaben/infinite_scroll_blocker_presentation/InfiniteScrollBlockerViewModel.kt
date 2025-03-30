package ly.com.tahaben.infinite_scroll_blocker_presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    private val _state = MutableStateFlow(InfiniteScrollBlockerState())
    val state = _state.asStateFlow()


    init {
        checkServiceStats()
        getTimeoutDuration()
        loadCountDownTime()
        getMsg()
        getMessages()
    }

    fun checkServiceStats() {
        checkAccessibilityPermissionStats()
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isServiceEnabled = infiniteScrollUseCases.isServiceEnabled() && infiniteScrollUseCases.isAccessibilityPermissionGranted()
                )
            }
        }
    }

    private fun checkAccessibilityPermissionStats() {
        _state.update {
            it.copy(
                isAccessibilityPermissionGranted = infiniteScrollUseCases.isAccessibilityPermissionGranted()
            )
        }
        Timber.d("state = ${_state.value.isAccessibilityPermissionGranted}")
    }

    fun setServiceStats(isEnabled: Boolean) {
        infiniteScrollUseCases.setServiceState(isEnabled)
        if (!_state.value.isAccessibilityPermissionGranted) {
            infiniteScrollUseCases.askForAccessibilityPermission()
        }
        _state.update {
            it.copy(
                isServiceEnabled = isEnabled
            )
        }
    }

    fun setTimeoutDuration(minutes: Int) {
        infiniteScrollUseCases.setTimeOutDuration(minutes)
        _state.update {
            it.copy(timeoutDuration = minutes)
        }
    }

    private fun getTimeoutDuration() {
        _state.update {
            it.copy(
                timeoutDuration = infiniteScrollUseCases.getTimeOutDuration()
            )
        }
    }

    fun checkIfAppearOnTopPermissionGranted() {
        _state.update {

            it.copy(
                isAppearOnTopPermissionGranted = infiniteScrollUseCases.isAppearOnTopPermissionGranted()
            )
        }
    }

    fun askForAppearOnTopPermission() {
        infiniteScrollUseCases.askForAppearOnTopPermission()
    }

    fun askForAccessibilityPermission() {
        infiniteScrollUseCases.askForAccessibilityPermission()
    }

    fun onEvent(event: InfiniteScrollEvent) {
        Timber.d("event: $event")
        when (event) {
            is InfiniteScrollEvent.OnCountDownDialogVisible -> {
                _state.update {
                    it.copy(
                        isCountDownDialogVisible = event.isVisible
                    )
                }
            }

            is InfiniteScrollEvent.SaveCountDownTime -> {
                Timber.d("saveCountDownTime: ${event.seconds}")
                saveCountDownTime(event.seconds)
            }

            is InfiniteScrollEvent.OnMsgDialogVisible -> {
                _state.update {
                    it.copy(
                        isMsgDialogVisible = event.isVisible
                    )
                }
            }

            is InfiniteScrollEvent.SaveMsg -> {
                selectMsg(event.msg)

            }

            is InfiniteScrollEvent.OnMsgTextFieldValueChange -> {
                _state.update {
                    it.copy(
                        msgTextFieldValue = event.text
                    )
                }
            }

            is InfiniteScrollEvent.OnAddMsg -> {
                addMsg(event.msg)
                _state.update {
                    it.copy(msgTextFieldValue = "")
                }
            }

            is InfiniteScrollEvent.OnDeleteMsg -> {
                deleteMsg(event.msg)
            }

            is InfiniteScrollEvent.OnMsgDropDownExpanded -> {
                _state.update {
                    it.copy(isMsgDropdownExpanded = event.isExpanded)
                }
            }

            is InfiniteScrollEvent.OnMsgSelected -> {
                selectMsg(event.msg)
                _state.update {
                    it.copy(isMsgDropdownExpanded = false)
                }
            }

            InfiniteScrollEvent.ResetMessages -> {
                resetMessages()
            }

            InfiniteScrollEvent.OnSwitchMsgDialogMode -> {
                _state.update {
                    it.copy(isMsgDialogInEditMode = !it.isMsgDialogInEditMode)
                }
            }
        }
    }

    private fun saveCountDownTime(seconds: Int) {
        preferences.setCountDownSeconds(seconds)
        _state.update {
            it.copy(
                countDownSeconds = seconds,
                isCountDownDialogVisible = false
            )
        }
    }

    private fun loadCountDownTime() {
        val countdown = infiniteScrollUseCases.getCountDown()
        _state.update {
            it.copy(countDownSeconds = countdown)
        }
    }

    private fun selectMsg(msg: String) {
        preferences.setCustomMessage(msg)
        _state.update {

            it.copy(
                selectedMessage = if (msg.isBlank()) UiText.StringResource(ly.com.tahaben.core.R.string.msg_random) else UiText.DynamicString(
                    msg
                )
            )
        }

    }

    private fun getMsg() {
        val msg = preferences.getCustomMessage()
        _state.update {
            it.copy(
                selectedMessage = if (msg.isBlank()) UiText.StringResource(ly.com.tahaben.core.R.string.msg_random) else UiText.DynamicString(
                    msg
                )
            )
        }
    }

    private fun getMessages() {
        _state.update {
            it.copy(msgSet = preferences.getMessagesArray())
        }
    }

    private fun resetMessages() {
        preferences.resetMessagesArray()
        getMessages()
    }

    private fun addMsg(msg: String) {
        preferences.addMessageToArray(msg)
        getMessages()
    }

    private fun deleteMsg(msg: String) {
        preferences.removeMessageFromArray(msg)
        getMessages()
    }


}