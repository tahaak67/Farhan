package ly.com.tahaben.farhan.service

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class AppLaunchDetectorTest {

    private companion object {
        const val LAUNCHER = "com.android.launcher"
        const val LAUNCHER_ACTIVITY = "com.android.launcher.HomeActivity"
        const val BROWSER = "com.brave.browser"
        const val BROWSER_ACTIVITY = "com.brave.browser.ChromeTabbedActivity"
        const val BROWSER_SETTINGS_ACTIVITY = "com.brave.browser.SettingsActivity"
        const val MESSENGER = "com.whatsapp"
        const val MESSENGER_ACTIVITY = "com.whatsapp.HomeActivity"
        const val IME = "com.google.android.inputmethod.latin"
        const val IME_WINDOW = "android.inputmethodservice.SoftInputWindow"
        const val CONTEXT_MENU_WINDOW = "android.widget.FrameLayout"
        const val OVERLAY_ACTIVITY = "ly.com.tahaben.launcher_presentation.wait.DelayedLaunchActivity"
        const val OWN_PACKAGE = "ly.com.tahaben.farhan"
    }

    private val activityClasses = setOf(
        "$LAUNCHER/$LAUNCHER_ACTIVITY",
        "$BROWSER/$BROWSER_ACTIVITY",
        "$BROWSER/$BROWSER_SETTINGS_ACTIVITY",
        "$MESSENGER/$MESSENGER_ACTIVITY",
        "$OWN_PACKAGE/$OVERLAY_ACTIVITY"
    )

    private var nowMillis = 0L
    private lateinit var detector: AppLaunchDetector

    @Before
    fun setUp() {
        nowMillis = 1.minutes.inWholeMilliseconds
        detector = AppLaunchDetector(
            isActivityClass = { pkg, cls -> "$pkg/$cls" in activityClasses },
            ignoredClassNames = setOf(OVERLAY_ACTIVITY),
            clock = { nowMillis }
        )
    }

    private fun advanceBy(millis: Long) {
        nowMillis += millis
    }

    @Test
    fun `launch from home after short dwell is detected with large away time`() {
        detector.onWindowStateChanged(LAUNCHER, LAUNCHER_ACTIVITY)
        advanceBy(2.seconds.inWholeMilliseconds)

        val result = detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)

        assertThat(result).isNotNull()
        assertThat(result!!.packageName).isEqualTo(BROWSER)
        assertThat(result.awayTimeMillis).isAtLeast(30.seconds.inWholeMilliseconds)
    }

    @Test
    fun `non-activity window does not count as app switch or change foreground`() {
        detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)
        advanceBy(1.minutes.inWholeMilliseconds)

        assertThat(detector.onWindowStateChanged(BROWSER, CONTEXT_MENU_WINDOW)).isNull()
        assertThat(detector.onWindowStateChanged(IME, IME_WINDOW)).isNull()
        // foreground is still the browser, so its next activity event is not a switch
        assertThat(detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)).isNull()
    }

    @Test
    fun `null className is ignored`() {
        assertThat(detector.onWindowStateChanged(BROWSER, null)).isNull()
    }

    @Test
    fun `in-app navigation to another activity of the same package is not a switch`() {
        detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)
        advanceBy(1.minutes.inWholeMilliseconds)

        assertThat(detector.onWindowStateChanged(BROWSER, BROWSER_SETTINGS_ACTIVITY)).isNull()
    }

    @Test
    fun `delayed launch overlay round trip does not re-trigger`() {
        detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)
        advanceBy(1.seconds.inWholeMilliseconds)
        // overlay activity appears on top, then the user taps "open app"
        assertThat(detector.onWindowStateChanged(OWN_PACKAGE, OVERLAY_ACTIVITY)).isNull()
        advanceBy(1.minutes.inWholeMilliseconds)

        assertThat(detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)).isNull()
    }

    @Test
    fun `quick round trip to another app reports small away time`() {
        detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)
        advanceBy(10.seconds.inWholeMilliseconds)
        detector.onWindowStateChanged(MESSENGER, MESSENGER_ACTIVITY)
        advanceBy(10.seconds.inWholeMilliseconds)

        val result = detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)

        assertThat(result).isNotNull()
        assertThat(result!!.awayTimeMillis).isEqualTo(10.seconds.inWholeMilliseconds)
    }

    @Test
    fun `returning after a long absence reports large away time`() {
        detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)
        advanceBy(10.seconds.inWholeMilliseconds)
        detector.onWindowStateChanged(MESSENGER, MESSENGER_ACTIVITY)
        advanceBy(2.minutes.inWholeMilliseconds)

        val result = detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)

        assertThat(result).isNotNull()
        assertThat(result!!.awayTimeMillis).isEqualTo(2.minutes.inWholeMilliseconds)
    }

    @Test
    fun `seeded foreground app does not trigger on its next window event`() {
        detector.seed(BROWSER)
        advanceBy(5.seconds.inWholeMilliseconds)

        assertThat(detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)).isNull()
    }

    @Test
    fun `launching another app after seeding is still detected`() {
        detector.seed(BROWSER)
        advanceBy(5.seconds.inWholeMilliseconds)

        val result = detector.onWindowStateChanged(MESSENGER, MESSENGER_ACTIVITY)

        assertThat(result).isNotNull()
        assertThat(result!!.packageName).isEqualTo(MESSENGER)
    }

    @Test
    fun `seeding with null keeps state unchanged`() {
        detector.seed(null)

        val result = detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)

        assertThat(result).isNotNull()
    }

    @Test
    fun `first event after reset is treated as a fresh launch`() {
        detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)
        advanceBy(5.seconds.inWholeMilliseconds)

        detector.reset()
        val result = detector.onWindowStateChanged(BROWSER, BROWSER_ACTIVITY)

        assertThat(result).isNotNull()
        assertThat(result!!.awayTimeMillis).isAtLeast(30.seconds.inWholeMilliseconds)
    }
}
