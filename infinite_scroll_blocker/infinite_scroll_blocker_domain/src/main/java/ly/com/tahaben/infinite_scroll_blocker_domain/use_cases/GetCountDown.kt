package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences

class GetCountDown (private val preferences: Preferences) {
    operator fun invoke(): Int{
        return preferences.getCountDownSeconds()
    }

}
