package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.preferences.Preferences


class LoadShouldShowOnBoarding(
    private val sharePref: Preferences
) {

    operator fun invoke(): Boolean {
        return sharePref.loadShouldShowOnBoarding()
    }
}