package ly.com.tahaben.screen_grayscale_presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.screen_grayscale_domain.use_cases.GrayscaleUseCases
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GrayscaleViewModel @Inject constructor(
    private val grayscaleUseCases: GrayscaleUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(GrayscaleState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        checkServiceStats()
        checkSecureSettingsPermissionStats()
        checkForRootAccess()
        checkAccessibilityPermissionStats()
    }

    fun checkServiceStats() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isServiceEnabled = grayscaleUseCases.isGrayscaleEnabled() && grayscaleUseCases.isAccessibilityPermissionGranted(),
                    isSecureSettingsPermissionGranted = grayscaleUseCases.isSecureSettingsPermissionGranted(),
                    isAccessibilityPermissionGranted = grayscaleUseCases.isAccessibilityPermissionGranted()
                )
            }
        }
    }

    private fun checkSecureSettingsPermissionStats() {
        _state.update {
            it.copy(
                isSecureSettingsPermissionGranted = grayscaleUseCases.isSecureSettingsPermissionGranted()
            )
        }
        Timber.d("secure permission: ${state.value.isSecureSettingsPermissionGranted}")
    }

    fun setServiceStats(isEnabled: Boolean) {
        grayscaleUseCases.setGrayscaleState(isEnabled)
        if (!state.value.isAccessibilityPermissionGranted) {
            askForAccessibilityPermission()
        }
        _state.update {
            it.copy(
                isServiceEnabled = isEnabled
            )
        }
    }

    fun askForAccessibilityPermission() {
        grayscaleUseCases.askForAccessibilityPermission()
    }

    private fun checkAccessibilityPermissionStats() {
        _state.update {
            it.copy(
                isAccessibilityPermissionGranted = grayscaleUseCases.isAccessibilityPermissionGranted()
            )
        }
        Timber.d("state = ${state.value.isAccessibilityPermissionGranted}")
    }

    fun askForSecureSettingsPermissionWithRoot() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            if (grayscaleUseCases.askForSecureSettingsPermission()) {
                _uiEvent.send(UiEvent.ShowSnackbar(UiText.StringResource(R.string.secure_permission_wroot_success)))
            } else {
                _uiEvent.send(UiEvent.ShowSnackbar(UiText.StringResource(R.string.secure_permission_wroot_error)))

            }
            _state.update { it.copy(isLoading = false) }
            checkSecureSettingsPermissionStats()


        }
    }

    fun checkForRootAccess() {
        _state.update {
            it.copy(isDeviceRooted = grayscaleUseCases.isDeviceRooted())
        }
    }
}