package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

import ly.com.tahaben.infinite_scroll_blocker_domain.util.AccessibilityServiceUtils

class AskForAccessibilityPermission(
    private val accessibilityUtil: AccessibilityServiceUtils
) {

    operator fun invoke() {
        accessibilityUtil.askForAccessibilityPermission()
    }
}