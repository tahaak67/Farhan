package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.infinite_scroll_blocker_domain.util.AccessibilityServiceUtils

class AskForAppearOnTopPermission(
    private val accessibilityUtils: AccessibilityServiceUtils
) {
    operator fun invoke() {
        accessibilityUtils.askForAppearOnTopPermission()
    }

}
