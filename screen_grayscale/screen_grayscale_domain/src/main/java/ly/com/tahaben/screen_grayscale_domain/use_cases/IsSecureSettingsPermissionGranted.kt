package ly.com.tahaben.screen_grayscale_domain.use_cases

import ly.com.tahaben.screen_grayscale_domain.util.GrayscaleUtil

class IsSecureSettingsPermissionGranted(
    private val accessibilityUtil: GrayscaleUtil
) {
    operator fun invoke(): Boolean {
        return accessibilityUtil.isSecureSettingsPermissionGranted()
    }

}
