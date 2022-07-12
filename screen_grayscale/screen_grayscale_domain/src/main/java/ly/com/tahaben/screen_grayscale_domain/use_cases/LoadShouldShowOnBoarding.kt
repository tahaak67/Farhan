package ly.com.tahaben.screen_grayscale_domain.use_cases

import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences

class LoadShouldShowOnBoarding(
    private val sharePref: Preferences
) {

    operator fun invoke(): Boolean {
        return sharePref.loadShouldShowOnBoarding()
    }
}