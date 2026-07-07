package ly.com.tahaben.screen_grayscale_domain.use_cases

data class GrayscaleUseCases(
    val saveShouldShowOnBoarding: SaveShouldShowOnBoarding,
    val loadShouldShowOnBoarding: LoadShouldShowOnBoarding,
    val askForSecureSettingsPermission: AskForSecureSettingsPermission,
    val getGrayScaleWhiteList: GetGrayscaleWhiteList,
    val getAppGrayscaleState: GetAppGrayscaleState,
    val setAppGrayscaleState: SetAppGrayscaleState,
    val isGrayscaleEnabled: IsGrayscaleEnabled,
    val isSecureSettingsPermissionGranted: IsSecureSettingsPermissionGranted,
    val setGrayscaleState: SetGrayscaleState,
    val getInstalledAppsList: GetInstalledAppsList,
    val askForAccessibilityPermission: AskForAccessibilityPermission,
    val isAccessibilityPermissionGranted: IsAccessibilityPermissionGranted,
    val isDeviceRooted: IsDeviceRooted
)
