package ly.com.tahaben.screen_grayscale_domain.use_cases

import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences

class RemovePackageFromGrayscaleWhiteList(
    private val sharedPref: Preferences
) {

    operator fun invoke(packageName: String) {
        sharedPref.removePackageFromInInfiniteScrollExceptions(packageName)
    }
}