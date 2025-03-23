package ly.com.tahaben.domain.use_case

data class MainScreenUseCases(
    val getDarkModePreference: GetDarkModePreference,
    val saveDarkModePreference: SaveDarkModePreference,
    val getThemeColorsPreference: GetThemeColorsPreference,
    val saveThemeColorsPreference: SaveThemeColorsPreference,
    val isMainSwitchEnabled: IsMainSwitchState,
    val setMainSwitchState: SetMainSwitchState,
    val loadShouldShowcaseAppearanceMenu: LoadShouldShowcaseAppearanceMenu,
    val saveShouldShowcaseAppearanceMenu: SaveShouldShowcaseAppearanceMenu,
    val loadShouldShowCombineDbDialog: LoadShouldShowCombineDbDialog
)
