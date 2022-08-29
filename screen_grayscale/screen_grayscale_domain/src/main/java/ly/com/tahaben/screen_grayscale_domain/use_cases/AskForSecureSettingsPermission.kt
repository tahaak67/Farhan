package ly.com.tahaben.screen_grayscale_domain.use_cases

import ly.com.tahaben.screen_grayscale_domain.util.GrayscaleUtil

class AskForSecureSettingsPermission(
    private val grayscaleUtil: GrayscaleUtil
) {

    operator fun invoke(): Boolean {
        return grayscaleUtil.getSecureSettingsPermissionWithRoot()
    }
}