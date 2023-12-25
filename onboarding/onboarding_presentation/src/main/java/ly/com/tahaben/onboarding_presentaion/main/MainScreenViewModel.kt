package ly.com.tahaben.onboarding_presentaion.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ly.com.tahaben.core.util.ThemeColors
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
        getThemeColorsSettings()
        getMainSwitchState()
        getShouldShowcaseAppearanceMenu()
    }

    private fun getUiAppearanceSettings() {
        val uiMode = useCases.getDarkModePreference()
        _mainScreenState.update {
            it.copy(
                uiMode = uiMode
            )
        }
    }

    private fun getThemeColorsSettings() {
        val themeColors = useCases.getThemeColorsPreference()
        if (themeColors != null) {
            _mainScreenState.update {
                it.copy(
                    themeColors = themeColors
                )
            }
        }
    }

    private fun getShouldShowcaseAppearanceMenu() {
        viewModelScope.launch {
            delay(2500) // todo: remove when showcaselayout is fixed
            _mainScreenState.update {
                it.copy(
                    shouldShowcaseAppearanceMenu = useCases.loadShouldShowcaseAppearanceMenu()
                )
            }
        }
    }

    private fun saveUiAppearanceSettings(uiModeAppearance: UIModeAppearance) {
        useCases.saveDarkModePreference(uiModeAppearance)
    }

    private fun saveThemeColorsSettings(themeColors: ThemeColors) {
        useCases.saveThemeColorsPreference(themeColors)
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

            MainScreenEvent.DismissThemeColorsDialog -> {
                _mainScreenState.update { state ->
                    state.copy(
                        isThemeColorsDialogVisible = false
                    )
                }
            }

            is MainScreenEvent.SaveThemeColorsMode -> {
                saveThemeColorsSettings(event.themeColors)
                _mainScreenState.update { state ->
                    state.copy(
                        themeColors = event.themeColors,
                        isThemeColorsDialogVisible = false
                    )
                }
            }

            MainScreenEvent.ShowThemeColorsDialog -> {
                _mainScreenState.update { state ->
                    state.copy(
                        isThemeColorsDialogVisible = true
                    )
                }
            }

            MainScreenEvent.AppearanceShowcaseFinished -> {
                useCases.saveShouldShowcaseAppearanceMenu(false)
                _mainScreenState.update {
                    it.copy(
                        shouldShowcaseAppearanceMenu = false
                    )
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