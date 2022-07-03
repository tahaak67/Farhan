package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences

class GetInfiniteScrollExceptions(
    private val sharedPref: Preferences
) {

    operator fun invoke(): Set<String> {
        return sharedPref.getInInfiniteScrollExceptionsList()
    }
}