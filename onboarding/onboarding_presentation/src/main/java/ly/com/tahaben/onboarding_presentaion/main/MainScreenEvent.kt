package ly.com.tahaben.onboarding_presentaion.main

import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.domain.model.UIModeAppearance

sealed class MainScreenEvent {
    data class SaveUiMode(val uiModeAppearance: UIModeAppearance) : MainScreenEvent()
    object ShowUiAppearanceDialog : MainScreenEvent()
    object DismissUiAppearanceDialog : MainScreenEvent()
    data class SaveMainSwitchState(val isEnabled: Boolean) : MainScreenEvent()
    data class ShowSnackBar(val message: UiText) : MainScreenEvent()
    object HideSnackBar : MainScreenEvent()
}
