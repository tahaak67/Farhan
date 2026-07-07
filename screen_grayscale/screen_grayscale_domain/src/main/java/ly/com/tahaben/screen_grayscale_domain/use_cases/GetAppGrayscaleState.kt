package ly.com.tahaben.screen_grayscale_domain.use_cases

import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.screen_grayscale_domain.model.GrayscaleAppState
import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences

class GetAppGrayscaleState(
    private val sharedPref: Preferences,
    private val installedAppsRepo: InstalledAppsRepository
) {

    /**
     * Apps without an explicit choice default to [GrayscaleAppState.LEAVE_AS_IS] when they are
     * system apps (app switcher, share sheet, launcher...) so they don't toggle the color filter
     * and destabilize the app the user is actually using (see issue #101).
     */
    operator fun invoke(packageName: String): GrayscaleAppState {
        return when {
            sharedPref.isPackageInInfiniteScrollExceptions(packageName) -> GrayscaleAppState.GRAYSCALE
            sharedPref.isPackageInGrayscaleAgnosticList(packageName) -> GrayscaleAppState.LEAVE_AS_IS
            sharedPref.isPackageInGrayscaleColoredList(packageName) -> GrayscaleAppState.COLOR
            installedAppsRepo.isSystemApp(packageName) -> GrayscaleAppState.LEAVE_AS_IS
            else -> GrayscaleAppState.COLOR
        }
    }
}
