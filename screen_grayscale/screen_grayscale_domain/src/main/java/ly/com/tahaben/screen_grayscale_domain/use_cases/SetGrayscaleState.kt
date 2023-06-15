package ly.com.tahaben.screen_grayscale_domain.use_cases

import ly.com.tahaben.screen_grayscale_domain.util.GrayscaleUtil

class SetGrayscaleState(
    private val grayscaleUtil: GrayscaleUtil
) {

    operator fun invoke(isEnabled: Boolean) {
        grayscaleUtil.setGrayscaleState(isEnabled)
    }
}