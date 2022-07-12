package ly.com.tahaben.screen_grayscale_domain.use_cases

import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.*

data class GrayscaleUseCases(
    val saveShouldShowOnBoarding: SaveShouldShowOnBoarding,
    val loadShouldShowOnBoarding: LoadShouldShowOnBoarding,
    val addPackageToGrayscaleWhiteList: AddPackageToGrayscaleWhiteList,
    val askForSecureSettingsPermission: AskForSecureSettingsPermission,
    val getGrayScaleWhiteList: GetGrayscaleWhiteList,
    val isPackageInGrayscaleWhiteList: IsPackageInGrayscaleWhiteList,
    val isGrayscaleEnabled: IsGrayscaleEnabled,
    val isSecureSettingsPermissionGranted: IsSecureSettingsPermissionGranted,
    val removePackageFromGrayscaleWhiteList: RemovePackageFromGrayscaleWhiteList,
    val setGrayscaleState: SetGrayscaleState,
    val getInstalledAppsList: GetInstalledAppsList,
    val askForAccessibilityPermission: AskForAccessibilityPermission,
    val isAccessibilityPermissionGranted: IsAccessibilityPermissionGranted,
    val isDeviceRooted: IsDeviceRooted
)
