package ly.com.tahaben.onboarding_presentaion.main

import ly.com.tahaben.core.util.ThemeColors
import ly.com.tahaben.domain.model.UIModeAppearance

data class MainScreenState(
    val uiMode: UIModeAppearance = UIModeAppearance.FOLLOW_SYSTEM,
    val isUiModeDialogVisible: Boolean = false,
    val themeColors: ThemeColors = ThemeColors.Classic,
    val isThemeColorsDialogVisible: Boolean = false,
    val isMainSwitchEnabled: Boolean = true,
    val shouldShowcaseAppearanceMenu: Boolean? = null
)
