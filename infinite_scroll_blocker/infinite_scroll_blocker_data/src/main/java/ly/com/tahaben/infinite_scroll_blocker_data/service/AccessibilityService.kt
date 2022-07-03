package ly.com.tahaben.infinite_scroll_blocker_data.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import ly.com.tahaben.core.R
import ly.com.tahaben.infinite_scroll_blocker_domain.model.ScrollViewInfo
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.InfiniteScrollUseCases
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class AccessibilityService : AccessibilityService() {

    private var recentScrollViews: HashMap<Int, ScrollViewInfo> = hashMapOf()

    @Inject
    lateinit var infiniteScrollUseCases: InfiniteScrollUseCases

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

    }

    private fun listenToScrollEvent(event: AccessibilityEvent) {
        Timber.d("event count = ${event.itemCount}")
        val maxY = if (event.itemCount > 0) {
            event.itemCount
        } else {
            -1
        }
        if (event.source == null || event.className == null) return

        if (maxY == -1) {
            //For video scrolling like Tiktok, (its not necessarily a viewPager some apps use
            // a recyclerview with a viewPager like behaviour)
            val viewPagerId = (
                    event.className.hashCode() +
                            event.packageName.hashCode() +
                            (event.source.viewIdResourceName?.hashCode() ?: 1) +
                            event.source.getBoundsInScreen(Rect()).hashCode()
                    )

            if (!recentScrollViews.containsKey(viewPagerId)) {
                recentScrollViews[viewPagerId] = ScrollViewInfo(maxY)
            } else {
                val scrollViewInfo = recentScrollViews[viewPagerId]!!
                val timeSinceLastUpdate =
                    TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - scrollViewInfo.updatedAt)
                if (timeSinceLastUpdate > 3) {
                    recentScrollViews.clear()
                    return
                }
                Timber.d("maxYview = ${scrollViewInfo.maxY}")
                Timber.d("maxY = $maxY")
                scrollViewInfo.updatedAt = System.currentTimeMillis()
                val scrollingTime =
                    TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - scrollViewInfo.addedAt)

                Timber.d("scrolling time = $scrollingTime")
                if (scrollingTime >= infiniteScrollUseCases.getTimeOutDuration()) {
                    showDialog(event.packageName.toString())
                    recentScrollViews.clear()
                }
            }
        } else {
            val scrollViewId = (
                    event.className.hashCode() +
                            event.packageName.hashCode() +
                            (event.source.viewIdResourceName?.hashCode() ?: 1) +
                            event.source.getBoundsInScreen(Rect()).hashCode()
                    )
            Timber.d("event scrollviewid = $scrollViewId")
            if (!recentScrollViews.containsKey(scrollViewId)) {
                recentScrollViews[scrollViewId] = ScrollViewInfo(maxY)
            } else {
                val scrollViewInfo = recentScrollViews[scrollViewId]!!
                val timeSinceLastUpdate =
                    TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - scrollViewInfo.updatedAt)
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
                    TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - scrollViewInfo.addedAt)

                Timber.d("scrolling time = $scrollingTime")
                Timber.d("isinfinite = $isInfinite")
                if (isInfinite && scrollingTime >= infiniteScrollUseCases.getTimeOutDuration()) {
                    showDialog(event.packageName.toString())
                    recentScrollViews.clear()
                }
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

    override fun onInterrupt() {

    }

    override fun onDestroy() {
        recentScrollViews.clear()
        super.onDestroy()
    }
}