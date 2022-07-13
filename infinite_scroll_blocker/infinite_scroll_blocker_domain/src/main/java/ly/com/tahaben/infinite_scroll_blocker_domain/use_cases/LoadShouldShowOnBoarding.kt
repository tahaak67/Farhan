package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences


class LoadShouldShowOnBoarding(
    private val sharePref: Preferences
) {

    operator fun invoke(): Boolean {
        return sharePref.loadShouldShowOnBoarding()
    }
}