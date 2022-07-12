package ly.com.tahaben.screen_grayscale_domain.use_cases

import ly.com.tahaben.screen_grayscale_domain.util.GrayscaleUtil

class AskForSecureSettingsPermission(
    private val accessibilityUtil: GrayscaleUtil
) {

    suspend operator fun invoke(): Boolean {
        return accessibilityUtil.getSecureSettingsPermissionWithRoot()
    }
}