package ly.com.tahaben.farhan.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import ly.com.tahaben.core.R
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

    private fun showDialog(packageName: String) {
        val bottomSheet = BottomSheetDialog(this)
        bottomSheet.apply {
            setContentView(R.layout.infinite_scroll_warning_layout)
            window!!.setType(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            )
            findViewById<TextView>(R.id.btn_continue)!!.setOnClickListener {
                dismiss()
            }
            findViewById<TextView>(R.id.btn_exclude_app)!!.setOnLongClickListener {
                infiniteScrollUseCases.addPackageToInfiniteScrollExceptions(packageName)
                Toast.makeText(
                    this@AccessibilityService,
                    getString(R.string.app_excluded),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
                true
            }
            findViewById<TextView>(R.id.btn_leave)!!.setOnClickListener {
                context.startActivity(
                    Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME)
                        .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                )
                dismiss()
            }
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }
        //to prevent crashes in case the user removed (draw on other apps permission) later on
        try {
            bottomSheet.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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