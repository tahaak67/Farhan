package ly.com.tahaben.farhan.service

import android.accessibilityservice.AccessibilityService
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.CountDownTimer
import android.provider.Settings
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ly.com.tahaben.core.R
import ly.com.tahaben.core.service.RunningService
import ly.com.tahaben.core.service.RunningServicesNotifier
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.core_ui.use_cases.UiUseCases
import ly.com.tahaben.core_ui.util.ComposeOverlayLifecycleOwner
import ly.com.tahaben.core_ui.util.isCurrentlyDark
import ly.com.tahaben.infinite_scroll_blocker_domain.model.ScrollViewInfo
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.InfiniteScrollUseCases
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.launcher_presentation.wait.DelayedLaunchActivity
import ly.com.tahaben.launcher_presentation.wait.DelayedUnlockActivity
import ly.com.tahaben.screen_grayscale_domain.model.GrayscaleAppState
import ly.com.tahaben.screen_grayscale_domain.use_cases.GrayscaleUseCases
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

const val DISPLAY_DALTONIZER_ENABLED = "accessibility_display_daltonizer_enabled"
const val DISPLAY_DALTONIZER = "accessibility_display_daltonizer"
const val NIGHT_DISPLAY_ACTIVATED = "night_display_activated"

// How long to let Night Light engage before/after clearing the daltonizer during the
// Pixel color-pipeline flush (see unGrayscaleScreen). Night Light fades in over ~3s,
// so 200ms only reaches a faint warmth while still pushing explicit matrix frames.
private val NIGHT_LIGHT_PULSE_DELAY = 200.milliseconds

// A delayed app re-opened within this window after leaving the foreground is not delayed
// again, so quick round-trips (checking a notification, fast app switching) stay smooth.
private val RELAUNCH_GRACE_PERIOD = 30.seconds

// SCREEN_ON and USER_PRESENT can both fire for a single unlock; triggers within this
// window after an unlock overlay are ignored so it is never shown twice.
private val UNLOCK_OVERLAY_DEBOUNCE = 5.seconds

@AndroidEntryPoint
class AccessibilityService : AccessibilityService() {

    private var recentScrollViews: HashMap<Int, ScrollViewInfo> = hashMapOf()
    private val softInputPackages = mutableListOf<String>()

    @Inject
    lateinit var infiniteScrollUseCases: InfiniteScrollUseCases

    @Inject
    lateinit var grayscaleUseCases: GrayscaleUseCases

    @Inject
    lateinit var uiUseCases: UiUseCases

    @Inject
    lateinit var launcherPref: Preference

    @Inject
    lateinit var runningServicesNotifier: RunningServicesNotifier

    private var isDelayedLaunchEnabled = false
    private var isDelayedUnlockEnabled = false
    private var lastUnlockOverlayShownAt = 0L

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var delayedPackages = emptySet<String>()

    private val unlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("unlock receiver: ${intent.action}, enabled: $isDelayedUnlockEnabled")
            if (!isDelayedUnlockEnabled) return
            if (intent.action == Intent.ACTION_SCREEN_ON) {
                // With any keyguard (even swipe) the unlock moment is ACTION_USER_PRESENT;
                // SCREEN_ON only counts as an unlock on devices with lock screen set to None.
                val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
                if (keyguardManager.isKeyguardLocked) return
            }
            val now = System.currentTimeMillis()
            if (now - lastUnlockOverlayShownAt < UNLOCK_OVERLAY_DEBOUNCE.inWholeMilliseconds) return
            lastUnlockOverlayShownAt = now
            showDelayedUnlockOverlay()
        }
    }

    override fun onCreate() {
        super.onCreate()
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).enabledInputMethodList.forEach {
            Timber.d("packageName: $it")
            softInputPackages.add(it.packageName)
        }
        registerReceiver(unlockReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_ON)
        })
        scope.launch {
            launch {
                launcherPref.isDelayedLaunchEnabled().collectLatest { enabled ->
                    Timber.d("isDelayedLaunchEnabled: $enabled")
                    isDelayedLaunchEnabled = enabled
                }
            }
            launch {
                launcherPref.isDelayedUnlockEnabled().collectLatest { enabled ->
                    Timber.d("isDelayedUnlockEnabled: $enabled")
                    isDelayedUnlockEnabled = enabled
                }
            }
            launch {
                launcherPref.getAppsInDLWhiteListAsFlow().collectLatest { newSet ->
                    Timber.d("new delayed packages: $newSet")
                    delayedPackages = newSet
                }
            }
        }
    }

    private val activityClassCache = HashMap<String, Boolean>()
    private val appLaunchDetector = AppLaunchDetector(
        isActivityClass = { pkg, cls ->
            activityClassCache.getOrPut("$pkg/$cls") {
                try {
                    packageManager.getActivityInfo(ComponentName(pkg, cls), 0)
                    true
                } catch (e: PackageManager.NameNotFoundException) {
                    false
                }
            }
        },
        ignoredClassNames = setOf(
            DelayedLaunchActivity::class.java.name,
            DelayedUnlockActivity::class.java.name
        )
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        // If the service (re)starts while the user is already inside an app, treat that app
        // as the current foreground so its next window event isn't mistaken for a launch.
        val foregroundPackage = rootInActiveWindow?.packageName?.toString()
        Timber.d("service connected, current foreground: $foregroundPackage")
        appLaunchDetector.seed(foregroundPackage)
        runningServicesNotifier.serviceStarted(RunningService.ACCESSIBILITY)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Timber.d("event received for class: ${event.className}")
        Timber.d("event type: ${AccessibilityEvent.eventTypeToString(event.eventType)} pn: ${event.packageName} fs? ${event.isFullScreen}")

        val packageName = event.packageName?.toString()
        if (packageName == null) {
            Timber.d("packageName is null, skipping event processing")
            return
        }


        val infiniteScrollBlockEnabled = runBlocking { infiniteScrollUseCases.isServiceEnabled() }
        if (infiniteScrollBlockEnabled && packageName != this.packageName) {
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                if (!infiniteScrollUseCases.isPackageInInfiniteScrollExceptions(packageName)) {
                    listenToScrollEvent(event)
                }
            }
        }
        val grayscaleEnabled = runBlocking { grayscaleUseCases.isGrayscaleEnabled() }
        if (grayscaleEnabled) {
            Timber.d("grayscale enabled")
            Timber.d("event type ${event.eventType}")

            //the statement below helps us determine if we need to skip this event or not
            if (event.isFullScreen || event.contentChangeTypes == AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED
                || !softInputPackages.contains(packageName)
            ) {
                val isForegroundWindowEvent = event.isFullScreen ||
                        event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                when (grayscaleUseCases.getAppGrayscaleState(packageName)) {
                    GrayscaleAppState.GRAYSCALE -> {
                        if (isForegroundWindowEvent) {
                            Timber.d("package $packageName in whitelist")
                            grayscaleScreen()
                        }
                    }

                    GrayscaleAppState.COLOR -> {
                        if (isForegroundWindowEvent) {
                            Timber.d("package $packageName not in whitelist")
                            unGrayscaleScreen()
                        }
                    }

                    GrayscaleAppState.LEAVE_AS_IS -> {
                        Timber.d("package $packageName is grayscale-agnostic, leaving filter as is")
                    }
                }
            }
        }
        Timber.d("isDelayed launch on?? $isDelayedLaunchEnabled")
        if (isDelayedLaunchEnabled && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val appSwitch =
                appLaunchDetector.onWindowStateChanged(packageName, event.className?.toString())
            if (appSwitch != null) {
                Timber.d("new app ${appSwitch.packageName}, away for ${appSwitch.awayTimeMillis.milliseconds}")
                if (appSwitch.awayTimeMillis > RELAUNCH_GRACE_PERIOD.inWholeMilliseconds &&
                    delayedPackages.contains(appSwitch.packageName)
                ) {
                    Timber.d("app in delayed launch whitelist!! ${appSwitch.packageName}")
                    showDelayedLaunchOverlay(appSwitch.packageName)
                }
            }
        }
    }

    private fun listenToScrollEvent(event: AccessibilityEvent) {
        Timber.d("event count = ${event.itemCount}")
        Timber.d("content description = ${event.contentDescription}")
        Timber.d("content describe = ${event.describeContents()}")
        val maxY = if (event.itemCount > 0) {
            event.itemCount
        } else {
            -1
        }
        if (event.source == null || event.className == null || maxY == -1) return

        val scrollViewId = (
                event.className.hashCode() +
                        event.packageName.hashCode() +
                        (event.source!!.viewIdResourceName?.hashCode() ?: 1) +
                        event.source!!.getBoundsInScreen(Rect()).hashCode()
                )
        Timber.d("event scrollviewid = $scrollViewId")
        if (!recentScrollViews.containsKey(scrollViewId)) {
            recentScrollViews[scrollViewId] = ScrollViewInfo(maxY)
        } else {
            val scrollViewInfo = recentScrollViews[scrollViewId]!!
            val timeSinceLastUpdate =
                (System.currentTimeMillis() - scrollViewInfo.updatedAt).milliseconds.inWholeMinutes
            Timber.d("updated timeSincelastupdate: $timeSinceLastUpdate")
            if (timeSinceLastUpdate > 3) {
                Timber.d("returning")
                recentScrollViews.clear()
                return
            }
            var isInfinite = false
            Timber.d("maxYview = ${scrollViewInfo.maxY}")
            Timber.d("maxY = $maxY")
            if (maxY > scrollViewInfo.maxY) {
                scrollViewInfo.timesGrown++
                Timber.d("updated times grown: ${scrollViewInfo.timesGrown}")
                scrollViewInfo.maxY = maxY
                scrollViewInfo.updatedAt = System.currentTimeMillis()
                Timber.d("updated at = ${scrollViewInfo.updatedAt}")
                if (scrollViewInfo.timesGrown >= 3) {
                    isInfinite = true
                }
            }

            val scrollingTime =
                (System.currentTimeMillis() - scrollViewInfo.addedAt).milliseconds.inWholeMinutes

            Timber.d("scrolling time = $scrollingTime")
            Timber.d("isinfinite = $isInfinite")
            if (isInfinite && scrollingTime >= infiniteScrollUseCases.getTimeOutDuration()) {
                showDialog(event.packageName.toString())
                recentScrollViews.clear()
            }
        }
    }

    private val windowManager get() = getSystemService(WINDOW_SERVICE) as WindowManager

    @OptIn(ExperimentalMaterial3Api::class)
    fun showDialog(packageName: String) {
        val countDownSeconds = infiniteScrollUseCases.getCountDown()
        val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val isDarkMode = uiUseCases.isDarkModeEnabled()
        val themeColors = uiUseCases.getCurrentThemeColors()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        val dialogMsg = infiniteScrollUseCases.getDialogMessage()
        val composeView = ComposeView(this)
        composeView.setContent {
            var currentCountDown by remember {
                mutableIntStateOf(countDownSeconds)
            }
            val dismissEnabled by remember {
                derivedStateOf { currentCountDown <= 0 }
            }
            val timer = object : CountDownTimer(
                countDownSeconds.seconds.inWholeMilliseconds,
                1.seconds.inWholeMilliseconds
            ) {
                override fun onTick(p0: Long) {
                    currentCountDown -= 1
                    Timber.d("tick: $currentCountDown")
                }

                override fun onFinish() {
                    currentCountDown = 0
                    Timber.d("on finished")
                }
            }
            DisposableEffect(key1 = Unit) {
                timer.start()
                onDispose {
                    timer.cancel()
                }
            }
            FarhanTheme(darkMode = isDarkMode.isCurrentlyDark(), colorStyle = themeColors) {
                val scope = rememberCoroutineScope()
//                val density = LocalDensity.current
//                val positionalThreshold = 56.dp
//                val velocityThreshold = 125.dp
                val bottomSheetState = rememberBottomSheetScaffoldState(
                    bottomSheetState = rememberStandardBottomSheetState(
                        initialValue = SheetValue.Expanded,
                        skipHiddenState = false,
//                        skipPartiallyExpanded = true,
                    )

//                        positionalThreshold = { with(density) { positionalThreshold.toPx() } },
//                        velocityThreshold = { with(density) { velocityThreshold.toPx() } },

                )
                val spacing = LocalSpacing.current
                fun dismissOverlay() {
                    scope.launch {
                        bottomSheetState.bottomSheetState.hide()
                    }.invokeOnCompletion {
                        windowManager.removeView(composeView)
                    }
                }

                BottomSheetScaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = bottomSheetState,
                    sheetDragHandle = { },
                    sheetContent = {
                        Column(
                            modifier = Modifier.padding(spacing.spaceExtraLarge),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(id = R.string.hey),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Image(
                                    modifier = Modifier.size(32.dp),
                                    painter = painterResource(id = R.drawable.farhan_transparent_bg),
                                    contentDescription = stringResource(
                                        id = R.string.farhan_icon
                                    ),
                                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface)
                                )
                            }
                            Row(Modifier.fillMaxWidth()) {
                                Text(modifier = Modifier.weight(0.8f), text = dialogMsg)
                                Spacer(modifier = Modifier.width(spacing.spaceSmall))
                                Crossfade(
                                    modifier = Modifier.weight(0.2f),
                                    targetState = dismissEnabled
                                ) { isEnabled ->
                                    if (!isEnabled) {
                                        AnimatedContent(targetState = currentCountDown) {
                                            Text(text = it.toString(), fontStyle = FontStyle.Italic)
                                        }
                                    }
                                }
                            }

                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    dismissOverlay()
                                    this@AccessibilityService.startActivity(
                                        Intent(Intent.ACTION_MAIN)
                                            .addCategory(Intent.CATEGORY_HOME)
                                            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                                    )
                                }) {
                                Text(text = stringResource(id = R.string.take_me_out_of_here))
                            }
                            Text(
                                modifier = Modifier.clickable { if (dismissEnabled) dismissOverlay() else Unit },
                                text = stringResource(id = R.string.continue_scrolling),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                infiniteScrollUseCases.addPackageToInfiniteScrollExceptions(
                                                    packageName
                                                )
                                                Toast.makeText(
                                                    this@AccessibilityService,
                                                    getString(R.string.app_excluded),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                dismissOverlay()
                                            }
                                        )
                                    },
                                text = stringResource(id = R.string.exclude_this_app_press_and_hold),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    },
                    sheetSwipeEnabled = false,
                    containerColor = BottomSheetDefaults.ScrimColor
                ) {

                }
            }
        }

        // Trick The ComposeView into thinking we are tracking lifecycle
        val viewModelStore = ViewModelStore()
        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        }
        val lifecycleOwner = ComposeOverlayLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        // This is required or otherwise the UI will not recompose
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        windowManager.addView(composeView, params)
    }

    private fun showDelayedLaunchOverlay(packageName: String) {
        Timber.d("showing delayed launch overlay")

        val intent = Intent(this.applicationContext, DelayedLaunchActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(DelayedLaunchActivity.PACKAGE_NAME, packageName)
        }
        startActivity(intent)
    }

    private fun showDelayedUnlockOverlay() {
        Timber.d("showing delayed unlock overlay")

        val intent = Intent(this.applicationContext, DelayedUnlockActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    // Serializes all color-filter writes so a night-light pulse in flight can never
    // interleave with (and undo) a grayscale re-apply from a fast app switch back.
    private val filterWriteLock = Mutex()
    private var unGrayscalePulseJob: Job? = null

    private fun grayscaleScreen() {
        if (this.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS")
            != PackageManager.PERMISSION_GRANTED
        ) return

        unGrayscalePulseJob?.cancel()
        val contentResolver = this.contentResolver
        scope.launch {
            filterWriteLock.withLock {
                val enabledSet = Settings.Secure.putInt(
                    contentResolver,
                    DISPLAY_DALTONIZER_ENABLED,
                    1
                )
                val modeSet = Settings.Secure.putInt(
                    contentResolver,
                    DISPLAY_DALTONIZER,
                    0
                )
                Timber.d("daltonizer set to grayscale (enabled write: $enabledSet, mode write: $modeSet)")
            }
        }
    }

    private fun unGrayscaleScreen() {
        if (this.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS")
            != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.d("Secure settings permission not granted, skipping")
            return
        }
        if (unGrayscalePulseJob?.isActive == true) return

        val contentResolver = this.contentResolver
        val wasGrayscaleOn =
            Settings.Secure.getInt(contentResolver, DISPLAY_DALTONIZER_ENABLED, 0) == 1
        val nightLightOn =
            Settings.Secure.getInt(contentResolver, NIGHT_DISPLAY_ACTIVATED, 0) == 1
       /*  Pixel color-pipeline bug (stock and GrapheneOS, tracked upstream as
         GrapheneOS/os-issue-tracker#6001): SurfaceFlinger ignores the "clear color
         transform" message sent when the last color matrix is removed, so disabling the
         daltonizer leaves the screen stuck in grayscale until reboot. Removing the matrix
         while another one (Night Light) is active works, because the composed transform
         is then pushed as an explicit matrix instead of a clear — and Night Light fades
         out through explicit near-identity frames, so its own removal is invisible.
         We replicate that here: briefly activate Night Light around the daltonizer clear,
         but only when actually leaving grayscale and Night Light isn't already on.
         */
        val needsPipelineFlush = wasGrayscaleOn && !nightLightOn &&
                Build.MANUFACTURER.equals("Google", ignoreCase = true)
        unGrayscalePulseJob = scope.launch {
            filterWriteLock.withLock {
                try {
                    if (needsPipelineFlush) {
                        Settings.Secure.putInt(contentResolver, NIGHT_DISPLAY_ACTIVATED, 1)
                        delay(NIGHT_LIGHT_PULSE_DELAY)
                    }
                    val enabledCleared = Settings.Secure.putInt(
                        contentResolver,
                        DISPLAY_DALTONIZER_ENABLED,
                        0
                    )
                    val modeCleared = Settings.Secure.putInt(
                        contentResolver,
                        DISPLAY_DALTONIZER,
                        -1
                    )
                    Timber.d(
                        "daltonizer disabled (enabled write: $enabledCleared, " +
                                "mode write: $modeCleared, pipeline flush: $needsPipelineFlush)"
                    )
                    if (needsPipelineFlush) {
                        delay(NIGHT_LIGHT_PULSE_DELAY)
                    }
                } finally {
                     /*
                     Runs on cancellation too (e.g. grayscaleScreen cancelled the pulse
                     because the user switched back), so Night Light is never left on.
                     */
                    if (needsPipelineFlush) {
                        Settings.Secure.putInt(contentResolver, NIGHT_DISPLAY_ACTIVATED, 0)
                    }
                }
            }
        }
    }

    override fun onInterrupt() {

    }

    override fun onUnbind(intent: Intent?): Boolean {
        recentScrollViews.clear()
        softInputPackages.clear()
        appLaunchDetector.reset()
        runningServicesNotifier.serviceStopped(RunningService.ACCESSIBILITY)
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        recentScrollViews.clear()
        softInputPackages.clear()
        appLaunchDetector.reset()
        runningServicesNotifier.serviceStopped(RunningService.ACCESSIBILITY)
        unregisterReceiver(unlockReceiver)
        scope.cancel()
        super.onDestroy()
    }
}