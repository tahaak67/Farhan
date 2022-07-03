package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences

class RemovePackageFromInfiniteScrollExceptions(
    private val sharedPref: Preferences
) {

    operator fun invoke(packageName: String) {
        sharedPref.removePackageFromInInfiniteScrollExceptions(packageName)
    }
}