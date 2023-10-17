package ly.com.tahaben.onboarding_presentaion.main

import ly.com.tahaben.domain.model.UIModeAppearance

sealed class MainScreenEvent {
    data class SaveUiMode(val uiModeAppearance: UIModeAppearance) : MainScreenEvent()
    object ShowUiAppearanceDialog : MainScreenEvent()
    object DismissUiAppearanceDialog : MainScreenEvent()
}
