package ly.com.tahaben.screen_grayscale_domain.use_cases

import ly.com.tahaben.screen_grayscale_domain.model.GrayscaleAppState
import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences

class SetAppGrayscaleState(
    private val sharedPref: Preferences
) {

    operator fun invoke(packageName: String, state: GrayscaleAppState) {
        when (state) {
            GrayscaleAppState.GRAYSCALE -> {
                sharedPref.savePackageToInfiniteScrollExceptions(packageName)
                sharedPref.removePackageFromGrayscaleAgnosticList(packageName)
                sharedPref.removePackageFromGrayscaleColoredList(packageName)
            }

            GrayscaleAppState.LEAVE_AS_IS -> {
                sharedPref.removePackageFromInInfiniteScrollExceptions(packageName)
                sharedPref.savePackageToGrayscaleAgnosticList(packageName)
                sharedPref.removePackageFromGrayscaleColoredList(packageName)
            }

            GrayscaleAppState.COLOR -> {
                sharedPref.removePackageFromInInfiniteScrollExceptions(packageName)
                sharedPref.removePackageFromGrayscaleAgnosticList(packageName)
                sharedPref.savePackageToGrayscaleColoredList(packageName)
            }
        }
    }
}
