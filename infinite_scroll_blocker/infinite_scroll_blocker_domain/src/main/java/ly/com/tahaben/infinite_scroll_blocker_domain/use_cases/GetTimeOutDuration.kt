package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences

class GetTimeOutDuration(
    private val sharedPref: Preferences
) {

    operator fun invoke(): Int {
        return sharedPref.getInfiniteScrollTimeOut()
    }
}