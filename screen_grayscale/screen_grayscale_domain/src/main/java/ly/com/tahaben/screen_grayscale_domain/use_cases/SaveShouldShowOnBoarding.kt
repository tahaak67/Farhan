package ly.com.tahaben.screen_grayscale_domain.use_cases

import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences

class SaveShouldShowOnBoarding(
    private val sharePref: Preferences
) {

    operator fun invoke(shouldShow: Boolean) {
        sharePref.saveShouldShowOnBoarding(shouldShow)
    }
}