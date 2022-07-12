package ly.com.tahaben.screen_grayscale_domain.use_cases

import ly.com.tahaben.screen_grayscale_domain.util.AccessibilityServiceUtils

class IsAccessibilityPermissionGranted(
    private val accessibilityUtil: AccessibilityServiceUtils
) {
    operator fun invoke(): Boolean {
        return accessibilityUtil.checkIfAccessibilityPermissionGranted()
    }

}
