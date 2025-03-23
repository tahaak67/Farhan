package ly.com.tahaben.onboarding_presentaion.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.domain.preferences.Preferences
import ly.com.tahaben.domain.use_case.MainScreenUseCases
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val useCases: MainScreenUseCases,
    private val preferences: Preferences
) : ViewModel() {

    private val _mainScreenState = MutableStateFlow(MainScreenState())
    val mainScreenState: StateFlow<MainScreenState> get() = _mainScreenState
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            loadUiPreferencesOnce()
            _isLoading.value = false
        }
        viewModelScope.launch {
            getThemeColorsSettings()
            getUiAppearanceSettings()
        }
        getMainSwitchState()
    }

    private suspend fun loadUiPreferencesOnce() {
        val uiMode = useCases.getDarkModePreference().first()
        val themeColors = useCases.getThemeColorsPreference().first()
        _mainScreenState.update {
            it.copy(
                uiMode = uiMode,
                themeColors = themeColors ?: ThemeColors.Classic
            )
        }
    }

    private suspend fun getUiAppearanceSettings() {
        useCases.getDarkModePreference().collectLatest { uiMode ->
            _mainScreenState.update {
                it.copy(
                    uiMode = uiMode
                )
            }
        }
    }

    private suspend fun getThemeColorsSettings() {
        useCases.getThemeColorsPreference().collectLatest { themeColors ->
            if (themeColors != null) {
                _mainScreenState.update {
                    it.copy(
                        themeColors = themeColors
                    )
                }
            }
        }
    }

    private fun getShouldShowcaseAppearanceMenu() {
        viewModelScope.launch {
            delay(1000)
            _mainScreenState.update {
                it.copy(
                    shouldShowcaseAppearanceMenu = useCases.loadShouldShowcaseAppearanceMenu()
                )
            }
        }
    }

    private fun saveUiAppearanceSettings(uiModeAppearance: UIModeAppearance) {
        viewModelScope.launch {
            useCases.saveDarkModePreference(uiModeAppearance)
        }
    }

    private fun saveThemeColorsSettings(themeColors: ThemeColors) {
        viewModelScope.launch {
            useCases.saveThemeColorsPreference(themeColors)
        }
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

            MainScreenEvent.OnScreenLaunched -> {
                getShouldShowcaseAppearanceMenu()
                _mainScreenState.update {
                    it.copy(
                        isCombineDbDialogVisible = useCases.loadShouldShowCombineDbDialog()
                    )
                }
            }

            MainScreenEvent.OnCombineDbAgreeClick -> {
                _mainScreenState.update {
                    it.copy(isCombiningDb = true)
                }
            }

            MainScreenEvent.OnDismissCombineDbDialog -> {
                _mainScreenState.update {
                    it.copy(isCombineDbDialogVisible = false)
                }
            }

            MainScreenEvent.OnExitApp -> Unit
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

    /**
     * Forces light more regardless of user settings, can be useful in case of using showcase layout where background needs
     * to be bright to show the tutorial.
     * @param on weather or not light mode should be forced, pass false to fall back to user settings.
     * */
    fun forceLightMode(on: Boolean) {
        if (on) {
            _mainScreenState.update {
                it.copy(
                    uiMode = UIModeAppearance.LIGHT_MODE
                )
            }
        } else {
            viewModelScope.launch {
                getUiAppearanceSettings()
            }
        }
    }
}