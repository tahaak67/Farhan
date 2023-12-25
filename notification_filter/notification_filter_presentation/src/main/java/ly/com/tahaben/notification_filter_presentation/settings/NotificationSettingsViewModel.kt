package ly.com.tahaben.notification_filter_presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ly.com.tahaben.notification_filter_domain.use_cases.NotificationFilterUseCases
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val notificationFilterUseCases: NotificationFilterUseCases
) : ViewModel() {

    var state by mutableStateOf(NotificationFilterSettingsState())
        private set

    init {
        getNotifyMeMinute()
        getNotifyMeHour()
        checkServiceStats()
        getNotifyState()
    }


    private fun checkServiceStats() {
        state = state.copy(
            isServiceEnabled = notificationFilterUseCases.checkIfNotificationServiceIsEnabled()
                    && notificationFilterUseCases.checkIfNotificationAccessIsGranted()
        )
    }

    fun setServiceStats(isEnabled: Boolean) {
        notificationFilterUseCases.setServiceState(isEnabled)
        state = state.copy(
            isServiceEnabled = isEnabled
        )
    }

    fun setNotifyMeTime(hour: Int, minutes: Int) {
        notificationFilterUseCases.setNotifyMeScheduleDate(-1)
        notificationFilterUseCases.setNotifyMeTime(hour, minutes)
        Timber.d("Notify me set to $hour:$minutes")

        state = state.copy(
            notifyMeHour = hour,
            notifyMeMinute = minutes,
            isNotifyMeEnabled = hour != -1
        )

    }

    private fun getNotifyMeHour() {
        state = state.copy(
            notifyMeHour = notificationFilterUseCases.getNotifyMeHour()
        )
    }

    private fun getNotifyState() {
        if (notificationFilterUseCases.getNotifyMeHour() == -1) {
            state = state.copy(
                isNotifyMeEnabled = false
            )
        } else {
            state = state.copy(
                isNotifyMeEnabled = true
            )
        }
    }

    private fun getNotifyMeMinute() {
        state = state.copy(
            notifyMeMinute = notificationFilterUseCases.getNotifyMeMinute()
        )
    }

    fun onEvent(event: NotificationSettingsEvent) {
        when (event) {
            NotificationSettingsEvent.CancelNotifyMe -> {
                setNotifyMeTime(-1, -1)
            }

            NotificationSettingsEvent.DismissNotifyMeTimePicker -> {
                state = state.copy(
                    isTimePickerVisible = false
                )
            }

            NotificationSettingsEvent.ShowNotifyMeTimePicker -> {
                state = state.copy(
                    isTimePickerVisible = true
                )
            }

            is NotificationSettingsEvent.SaveNotifyMeTime -> {
                setNotifyMeTime(event.hour, event.min)
                state = state.copy(
                    isTimePickerVisible = false
                )
            }
        }

    }
}