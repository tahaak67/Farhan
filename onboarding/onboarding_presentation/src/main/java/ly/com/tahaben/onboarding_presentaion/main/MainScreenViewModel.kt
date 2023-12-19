package ly.com.tahaben.onboarding_presentaion.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.domain.model.UIModeAppearance
import ly.com.tahaben.domain.use_case.MainScreenUseCases
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val useCases: MainScreenUseCases
) : ViewModel() {

    private val _mainScreenState = MutableStateFlow(MainScreenState())
    val mainScreenState: StateFlow<MainScreenState> get() = _mainScreenState
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        getUiAppearanceSettings()
        getMainSwitchState()
    }

    private fun getUiAppearanceSettings() {
        val uiMode = useCases.getDarkModePreference()
        _mainScreenState.update {
            it.copy(
                uiMode = uiMode
            )
        }
    }

    private fun saveUiAppearanceSettings(uiModeAppearance: UIModeAppearance) {
        useCases.saveDarkModePreference(uiModeAppearance)
    }

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.DismissUiAppearanceDialog -> {
                _mainScreenState.update { state ->
                    state.copy(
                        isUiModeDialogVisible = false
                    )
                }
            }

            is MainScreenEvent.SaveUiMode -> {
                saveUiAppearanceSettings(event.uiModeAppearance)
                _mainScreenState.update { state ->
                    state.copy(
                        uiMode = event.uiModeAppearance,
                        isUiModeDialogVisible = false
                    )
                }
            }

            MainScreenEvent.ShowUiAppearanceDialog -> {
                _mainScreenState.update { state ->
                    state.copy(
                        isUiModeDialogVisible = true
                    )
                }
            }

            is MainScreenEvent.SaveMainSwitchState -> {
                saveMainSwitchState(event.isEnabled)
                _mainScreenState.update { state ->
                    state.copy(
                        isMainSwitchEnabled = event.isEnabled
                    )
                }
            }

            is MainScreenEvent.ShowSnackBar -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.ShowSnackbar(event.message))
                }
            }

            MainScreenEvent.HideSnackBar -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.HideSnackBar)
                }
            }
        }
    }

    fun getMainSwitchState() {
        _mainScreenState.update { state ->
            state.copy(
                isMainSwitchEnabled = useCases.isMainSwitchEnabled()
            )
        }
    }

    private fun saveMainSwitchState(isEnabled: Boolean) {
        useCases.setMainSwitchState(isEnabled)
    }

}