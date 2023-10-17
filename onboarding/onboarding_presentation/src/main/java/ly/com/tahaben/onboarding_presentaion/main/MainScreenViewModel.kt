package ly.com.tahaben.onboarding_presentaion.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ly.com.tahaben.domain.model.UIModeAppearance
import ly.com.tahaben.domain.use_case.MainScreenUseCases
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val useCases: MainScreenUseCases
) : ViewModel() {

    private val _mainScreenState = MutableStateFlow(MainScreenState())
    val mainScreenState: StateFlow<MainScreenState> get() = _mainScreenState

    init {
        getUiAppearanceSettings()
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
        }
    }

}