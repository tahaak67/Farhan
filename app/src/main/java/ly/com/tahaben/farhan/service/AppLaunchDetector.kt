package ly.com.tahaben.farhan.service

/**
 * Tracks the current foreground app from TYPE_WINDOW_STATE_CHANGED events and reports
 * genuine app switches.
 *
 * Only events whose className resolves to a real Activity count as app switches — dialogs,
 * popup menus and IME windows don't resolve and are ignored, so they can never be mistaken
 * for an app launch (issue #99).
 *
 * Pure Kotlin (no android.* imports) so it can be unit tested on the JVM; the Activity
 * lookup is injected as [isActivityClass].
 */
class AppLaunchDetector(
    private val isActivityClass: (packageName: String, className: String) -> Boolean,
    private val ignoredClassNames: Set<String>,
    private val clock: () -> Long = System::currentTimeMillis,
) {

    data class AppSwitch(val packageName: String, val awayTimeMillis: Long)

    private var currentForegroundPackage: String? = null
    private val foregroundLeftAt = mutableMapOf<String, Long>()

    /**
     * Feed every TYPE_WINDOW_STATE_CHANGED event here.
     *
     * @return an [AppSwitch] when a new app comes to the foreground, null otherwise.
     * [AppSwitch.awayTimeMillis] is how long that app has been out of the foreground
     * (effectively infinite for an app seen for the first time).
     */
    fun onWindowStateChanged(packageName: String, className: String?): AppSwitch? {
        if (className == null || className in ignoredClassNames) return null
        if (!isActivityClass(packageName, className)) return null
        if (packageName == currentForegroundPackage) return null
        val now = clock()
        currentForegroundPackage?.let { foregroundLeftAt[it] = now }
        currentForegroundPackage = packageName
        return AppSwitch(packageName, now - (foregroundLeftAt[packageName] ?: 0L))
    }

    /**
     * Marks [packageName] as the current foreground app without reporting a switch.
     * Called when the accessibility service (re)connects so an app the user is already
     * inside isn't mistaken for a fresh launch on its next window event.
     */
    fun seed(packageName: String?) {
        if (packageName != null) {
            currentForegroundPackage = packageName
        }
    }

    fun reset() {
        currentForegroundPackage = null
        foregroundLeftAt.clear()
    }
}
