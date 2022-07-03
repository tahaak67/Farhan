package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences

class SetTimeOutDuration(
    private val sharedPref: Preferences
) {

    operator fun invoke(minutes: Int) {
        sharedPref.setInfiniteScrollTimeOut(minutes)
    }
}