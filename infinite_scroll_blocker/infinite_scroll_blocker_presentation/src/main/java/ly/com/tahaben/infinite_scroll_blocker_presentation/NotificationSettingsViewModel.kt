package ly.com.tahaben.infinite_scroll_blocker_presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    //private val notificationFilterUseCases: NotificationFilterUseCases
) : ViewModel() {
/*

    var state by mutableStateOf(NotificationFilterSettingsState())
        private set

    init {
        getNotifyMeMinute()
        getNotifyMeHour()
        checkServiceStats()
        getNotifyState()
    }


    fun checkServiceStats() {
        state = state.copy(
            isServiceEnabled = notificationFilterUseCases.checkIfNotificationServiceIsEnabled()
        )
    }
    fun setServiceStats(isEnabled: Boolean) {
        notificationFilterUseCases.setServiceState(isEnabled)
        state = state.copy(
            isServiceEnabled = isEnabled
        )
    }

    fun setNotifyMeTime(hour: Int, minutes: Int) {
        notificationFilterUseCases.setNotifyMeTime(hour, minutes)
        state = state.copy(
            notifyMeHour = hour,
            notifyMeMinute = minutes
        )
    }

    fun getNotifyMeHour() {
        state = state.copy(
            notifyMeHour = notificationFilterUseCases.getNotifyMeHour()
        )
    }

    fun getNotifyState() {
        if (notificationFilterUseCases.getNotifyMeHour() == -1){
            state = state.copy(
                isNotifyMeEnabled = false
            )
        }else {
            state = state.copy(
                isNotifyMeEnabled = true
            )
        }
    }

    fun getNotifyMeMinute() {
        state = state.copy(
            notifyMeMinute = notificationFilterUseCases.getNotifyMeMinute()
        )
    }
*/


}