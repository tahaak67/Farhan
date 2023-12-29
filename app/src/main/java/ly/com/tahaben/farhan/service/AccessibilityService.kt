package ly.com.tahaben.farhan.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.core_ui.util.ComposeOverlayLifecycleOwner
import ly.com.tahaben.core_ui.util.isCurrentlyDark
import ly.com.tahaben.infinite_scroll_blocker_domain.model.ScrollViewInfo
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.InfiniteScrollUseCases
import ly.com.tahaben.screen_grayscale_domain.use_cases.GrayscaleUseCases
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

const val DISPLAY_DALTONIZER_ENABLED = "accessibility_display_daltonizer_enabled"
const val DISPLAY_DALTONIZER = "accessibility_display_daltonizer"

@AndroidEntryPoint
class AccessibilityService : AccessibilityService() {

    private var recentScrollViews: HashMap<Int, ScrollViewInfo> = hashMapOf()
    private val softInputPackages = mutableListOf<String>()

    @Inject
    lateinit var infiniteScrollUseCases: InfiniteScrollUseCases

    @Inject
    lateinit var grayscaleUseCases: GrayscaleUseCases

    override fun onCreate() {
        super.onCreate()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).enabledInputMethodList.forEach {
            Timber.d("packageName: $it")
            softInputPackages.add(it.packageName)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Timber.d("event received for class: ${event.className}")
        Timber.d("event type: ${event.eventType}")
        if (infiniteScrollUseCases.isServiceEnabled()) {
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                if (!infiniteScrollUseCases.isPackageInInfiniteScrollExceptions(event.packageName.toString())) {
                    listenToScrollEvent(event)
                }
            }
        }
        if (grayscaleUseCases.isGrayscaleEnabled()) {
            Timber.d("grayscale enabled")
            Timber.d("event type ${event.eventType}")

            //the statement below helps us determine if we need to skip this event or not
            if (event.isFullScreen || event.contentChangeTypes == AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED
                || !softInputPackages.contains(event.packageName)
            ) {
                if (grayscaleUseCases.isPackageInGrayscaleWhiteList(
                        event.packageName?.toString() ?: return
                    )
                ) {
                    Timber.d("package ${event.packageName} in whitelist")
                    grayscaleScreen()
                } else if (event.isFullScreen) {
                    Timber.d("package ${event.packageName} not in whitelist")
                    unGrayscaleScreen()
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
        val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val isDarkMode = infiniteScrollUseCases.isDarkModeEnabled()
        val themeColors = infiniteScrollUseCases.getCurrentThemeColors()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        val composeView = ComposeView(this)
        composeView.setContent {
            FarhanTheme(darkMode = isDarkMode.isCurrentlyDark(), colorStyle = themeColors) {
                val scope = rememberCoroutineScope()
                val bottomSheetState = rememberBottomSheetScaffoldState(
                    bottomSheetState = SheetState(
                        initialValue = SheetValue.Expanded,
                        skipHiddenState = false,
                        skipPartiallyExpanded = true
                    )
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
                            Text(
                                text = stringResource(id = R.string.infinite_scroll_dialog_message)
                            )
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
                            TextButton(onClick = {
                                dismissOverlay()
                            }) {
                                Text(
                                    text = stringResource(id = R.string.continue_scrolling),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
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

    private fun grayscaleScreen() {
        if (this.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS")
            != PackageManager.PERMISSION_GRANTED
        ) return

        val contentResolver = this.contentResolver
        Settings.Secure.putInt(
            contentResolver,
            DISPLAY_DALTONIZER_ENABLED,
            1
        )
        Settings.Secure.putInt(
            contentResolver,
            DISPLAY_DALTONIZER,
            0
        )
    }

    private fun unGrayscaleScreen() {
        if (this.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS")
            != PackageManager.PERMISSION_GRANTED
        ) return

        val contentResolver = this.contentResolver
        Settings.Secure.putInt(
            contentResolver,
            DISPLAY_DALTONIZER_ENABLED,
            0
        )
        Settings.Secure.putInt(
            contentResolver,
            DISPLAY_DALTONIZER,
            -1
        )
    }

    override fun onInterrupt() {

    }

    override fun onUnbind(intent: Intent?): Boolean {
        recentScrollViews.clear()
        softInputPackages.clear()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        recentScrollViews.clear()
        softInputPackages.clear()
        super.onDestroy()
    }
}