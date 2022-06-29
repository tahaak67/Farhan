package ly.com.tahaben.core.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent

class AccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        listenToScrollEvents(event)
    }

    private fun listenToScrollEvents(event: AccessibilityEvent) {
        val maxY = if (event.itemCount > 0) {
            event.itemCount
        } else {
            -1
        }
        if (maxY == -1 || event.source == null || event.className == null) return

        val scrollViewId = (
                event.className.hashCode() +
                        event.packageName.hashCode() +
                        event.source.viewIdResourceName.hashCode() +
                        event.source.getBoundsInScreen(Rect()).hashCode()
                )


    }

    override fun onInterrupt() {

    }
}