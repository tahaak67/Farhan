package ly.com.tahaben.launcher_presentation.time_limiter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ly.com.tahaben.launcher_domain.use_case.time_limit.TimeLimitUseCases
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TimeLimiterSettingsViewModel @Inject constructor(
    private val timeLimitUseCases: TimeLimitUseCases
) : ViewModel() {

    /*  var state by mutableStateOf(TimeLimiterSettingsState())
          private set*/

    private var _state = MutableStateFlow(TimeLimiterSettingsState())
    val state = _state
        .onStart { checkTimeLimiterStats() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = TimeLimiterSettingsState()
        )

    /*init {
        checkTimeLimiterStats()
    }*/


    fun checkTimeLimiterStats() {
        checkIfAppearOnTopPermissionGranted()
        _state.update {
            it.copy(
                isTimeLimiterEnabled = timeLimitUseCases.isTimeLimiterEnabled()
            )
        }
        refreshService()
    }

    fun refreshService() {
        if (_state.value.isTimeLimiterEnabled) {
            timeLimitUseCases.startTimeLimitService()
        } else {
            timeLimitUseCases.stopTimeLimitService()
        }
    }

    fun setTimeLimiterEnabled(isEnabled: Boolean) {
        timeLimitUseCases.setTimeLimiterEnabled(isEnabled)

        _state.update {
            it.copy(
                isTimeLimiterEnabled = isEnabled
            )
        }
        refreshService()
    }

    private fun checkAccessibilityPermissionStats() {
        // todo: remove accessability checks

        _state.update {
            it.copy(
                isAccessibilityPermissionGranted = timeLimitUseCases.isAccessibilityPermissionGranted()
            )
        }
        Timber.d("state = ${_state.value.isAccessibilityPermissionGranted}")
    }

    fun checkIfAppearOnTopPermissionGranted() {

        _state.update {
            it.copy(
                isAppearOnTopPermissionGranted = timeLimitUseCases.isAppearOnTopPermissionGranted()

            )
        }
    }

    fun askForAppearOnTopPermission() {
        timeLimitUseCases.askForAppearOnTopPermission()
    }

    fun askForAccessibilityPermission() {
        timeLimitUseCases.askForAccessibilityPermission()
    }

}