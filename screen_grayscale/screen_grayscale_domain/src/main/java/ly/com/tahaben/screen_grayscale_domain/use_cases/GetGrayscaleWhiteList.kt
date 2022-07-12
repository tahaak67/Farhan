package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences

class GetGrayscaleWhiteList(
    private val sharedPref: Preferences
) {

    operator fun invoke(): Set<String> {
        return sharedPref.getInInfiniteScrollExceptionsList()
    }
}