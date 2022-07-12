package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences

class RemovePackageFromGrayscaleWhiteList(
    private val sharedPref: Preferences
) {

    operator fun invoke(packageName: String) {
        sharedPref.removePackageFromInInfiniteScrollExceptions(packageName)
    }
}