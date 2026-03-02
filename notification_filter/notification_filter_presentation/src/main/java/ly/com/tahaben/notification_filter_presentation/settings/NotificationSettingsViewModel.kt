package ly.com.tahaben.notification_filter_presentation.settings

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ly.com.tahaben.notification_filter_domain.preferences.Preferences
import ly.com.tahaben.notification_filter_domain.use_cases.NotificationFilterUseCases
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val notificationFilterUseCases: NotificationFilterUseCases,
    private val preferences: Preferences
) : ViewModel() {

    var _state = MutableStateFlow(NotificationFilterSettingsState())
    val state = _state.asStateFlow()

    private val _event = Channel<UiEventNotificationSettings>()
    val event = _event.receiveAsFlow()

    init {
        getNotifyMeMinute()
        getNotifyMeHour()
        checkServiceStats()
        getNotifyState()
        performPermissionSilentChecks()
        shouldShowWarningDialog()
    }


    private fun checkServiceStats() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isServiceEnabled = notificationFilterUseCases.checkIfNotificationServiceIsEnabled()
                            && notificationFilterUseCases.checkIfNotificationAccessIsGranted()
                )
            }
        }
    }

    fun setServiceStats(isEnabled: Boolean) {
        notificationFilterUseCases.setServiceState(isEnabled)
        _state.update {
            it.copy(
                isServiceEnabled = isEnabled
            )
        }
    }

    fun setNotifyMeTime(hour: Int, minutes: Int) {
        notificationFilterUseCases.setNotifyMeScheduleDate(-1)
        notificationFilterUseCases.setNotifyMeTime(hour, minutes)
        Timber.d("Notify me set to $hour:$minutes")

        _state.update {
            it.copy(
                notifyMeHour = hour,
                notifyMeMinute = minutes,
                isNotifyMeEnabled = hour != -1
            )
        }

        if (hour != -1 && minutes != -1) {
            viewModelScope.launch {
                _event.send(UiEventNotificationSettings.NotifyMeEnabled)
            }
        }

    }

    private fun getNotifyMeHour() {
        _state.update {
            it.copy(
                notifyMeHour = notificationFilterUseCases.getNotifyMeHour()
            )
        }
    }

    private fun getNotifyState() {
        val t = notificationFilterUseCases.getNotifyMeHour()
        Timber.d("Notify me hour is $t")
        if (t == -1) {
            _state.update {
                it.copy(
                    isNotifyMeEnabled = false
                )
            }
        } else {
            _state.update {
                it.copy(
                    isNotifyMeEnabled = true
                )
            }
        }
    }

    private fun getNotifyMeMinute() {
        _state.update {
            it.copy(
                notifyMeMinute = notificationFilterUseCases.getNotifyMeMinute()
            )
        }
    }

    fun onEvent(event: NotificationSettingsEvent) {
        when (event) {
            NotificationSettingsEvent.CancelNotifyMe -> {
                setNotifyMeTime(-1, -1)
            }

            NotificationSettingsEvent.DismissNotifyMeTimePicker -> {
                _state.update {
                    it.copy(
                        isTimePickerVisible = false
                    )
                }
            }

            NotificationSettingsEvent.ShowNotifyMeTimePicker -> {
                _state.update {
                    it.copy(
                        isTimePickerVisible = true
                    )
                }
            }

            is NotificationSettingsEvent.SaveNotifyMeTime -> {
                setNotifyMeTime(event.hour, event.min)
                _state.update {
                    it.copy(
                        isTimePickerVisible = false
                    )

                }
            }

            NotificationSettingsEvent.DismissPermissionDialog -> {
                Timber.d("remove permission called")
                _state.value.visiblePermissionDialogQueue.removeAt(0)
            }

            is NotificationSettingsEvent.DeclinedPermission -> {
                Timber.d("declined permission is: ${event.permission}")
                _state.value.declinedPermissions.add(event.permission)
            }

            NotificationSettingsEvent.OpenExactAlarmPermissionPage -> {
                openExactAlarmsPermissionScreen()
            }

            is NotificationSettingsEvent.DismissWarningDialog -> {
                _state.update {
                    it.copy(
                        isWarningDialogVisible = false
                    )
                }
                if (event.doNotShowAgain) {
                    preferences.setSettingsShouldShowWarning(false)
                }
            }

            is NotificationSettingsEvent.OnShouldShowcase -> {
                _state.update {
                    it.copy(
                        isShowcaseOn = event.showcase
                    )
                }
            }

        }
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !_state.value.visiblePermissionDialogQueue.contains(permission)) {
            _state.value.visiblePermissionDialogQueue.add(permission)
        }
        Timber.d("permissions queue: ${_state.value.visiblePermissionDialogQueue} size: ${_state.value.visiblePermissionDialogQueue.size}")
    }

    fun openAppSettings() {
        notificationFilterUseCases.openAppSettings()
    }

    fun checkExactAlarmsPermissionGranted() {
        if (!notificationFilterUseCases.canScheduleExactAlarms()) {
            _state.value.visiblePermissionDialogQueue.add(Manifest.permission.SCHEDULE_EXACT_ALARM)
        }
    }

    fun exactAlarmsSilentCheck() {
        if (!notificationFilterUseCases.canScheduleExactAlarms()) {
            _state.value.declinedPermissions.add(Manifest.permission.SCHEDULE_EXACT_ALARM)
        }
    }

    fun openExactAlarmsPermissionScreen() {
        notificationFilterUseCases.openExactAlarmsPermissionScreen()
    }

    private fun performPermissionSilentChecks() {
        viewModelScope.launch {
            _event.send(UiEventNotificationSettings.PerformSilentChecks)
        }
    }

    private fun shouldShowWarningDialog() {
        _state.update {
            it.copy(
                isWarningDialogVisible = preferences.getSettingsShouldShowWarning()
            )
        }
    }

}

