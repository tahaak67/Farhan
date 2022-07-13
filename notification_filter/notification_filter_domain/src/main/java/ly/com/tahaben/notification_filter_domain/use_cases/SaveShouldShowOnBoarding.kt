package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.preferences.Preferences


class SaveShouldShowOnBoarding(
    private val sharePref: Preferences
) {

    operator fun invoke(shouldShow: Boolean) {
        sharePref.saveShouldShowOnBoarding(shouldShow)
    }
}