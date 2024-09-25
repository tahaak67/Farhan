package ly.com.tahaben.onboarding_presentaion.main

import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core.util.UiText

sealed class MainScreenEvent {
    data class SaveUiMode(val uiModeAppearance: UIModeAppearance) : MainScreenEvent()
    object ShowUiAppearanceDialog : MainScreenEvent()
    object DismissUiAppearanceDialog : MainScreenEvent()
    data class SaveThemeColorsMode(val themeColors: ThemeColors) : MainScreenEvent()
    object ShowThemeColorsDialog : MainScreenEvent()
    object DismissThemeColorsDialog : MainScreenEvent()
    data class SaveMainSwitchState(val isEnabled: Boolean) : MainScreenEvent()
    data class ShowSnackBar(val message: UiText) : MainScreenEvent()
    object HideSnackBar : MainScreenEvent()
    object AppearanceShowcaseFinished : MainScreenEvent()
    object OnScreenLaunched : MainScreenEvent()
    object OnDismissCombineDbDialog: MainScreenEvent()
    object OnCombineDbAgreeClick: MainScreenEvent()
    object OnExitApp: MainScreenEvent()
}
