package ly.com.tahaben.launcher_data.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.core_ui.use_cases.UiUseCases
import ly.com.tahaben.core_ui.util.ComposeOverlayLifecycleOwner
import ly.com.tahaben.core_ui.util.isCurrentlyDark
import ly.com.tahaben.launcher_domain.model.TimeLimit
import ly.com.tahaben.launcher_domain.use_case.time_limit.TimeLimitUseCases
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 11,Feb,2023
 */
@AndroidEntryPoint
class TimeLimitService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false
    private var serviceScope: CoroutineScope? = null
    private var timeLimitedApps: HashMap<String, TimeLimit> = hashMapOf()

    @Inject
    lateinit var timeLimitUseCases: TimeLimitUseCases

    @Inject
    lateinit var uiUseCases: UiUseCases


    override fun onBind(intent: Intent): IBinder? {
        Timber.d("Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand executed with startId: $startId")
        if (intent != null) {
            val action = intent.action
            Timber.d("using an intent with action $action")
            when (action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                else -> Timber.d("This should never happen. No action in the received intent")
            }
        } else {
            Timber.d(
                "with a null intent. It has been probably restarted by the system."
            )
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("The service has been created")
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notification)
        }
        serviceScope = CoroutineScope(Dispatchers.IO)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope = null
        Timber.d("The service has been destroyed")
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, TimeLimitService::class.java).also {
            it.setPackage(packageName)
        }
        val restartServicePendingIntent: PendingIntent =
            PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_IMMUTABLE)
        applicationContext.getSystemService(Context.ALARM_SERVICE)
        val alarmService: AlarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1000,
            restartServicePendingIntent
        )
    }

    private fun startService() {
        if (isServiceStarted) return
        Timber.d("Starting the foreground service task")
        Toast.makeText(this, ".", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        // setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "${this@TimeLimitService::class.java.name}::lock"
                ).apply {
                    acquire(10.minutes.inWholeMilliseconds)
                }
            }

        // we're starting a loop in a coroutine
        serviceScope?.launch {
            while (isServiceStarted) {
                if (timeLimitUseCases.isTimeLimiterEnabled()) {
                    val packageName = getForegroundAppPackageName(this@TimeLimitService)
                    Timber.d("foreground pkg: $packageName")
                    packageName?.let {
                        val newLimit =
                            timeLimitUseCases.getTimeLimitForPackage(packageName)
                        if (newLimit != null) {
                            timeLimitedApps[newLimit.packageName] = newLimit
                            timeLimitUseCases.removeTimeLimitFromDb(newLimit)
                        }
                        Timber.d("newLimit: $newLimit")
                        if (timeLimitedApps.contains(packageName)) {
                                Timber.d("newLimit: contains!")
                                val timedApp = timeLimitedApps[packageName]!!
                                if (System.currentTimeMillis() - timedApp.timeAtAddingInMilli >= timedApp.timeLimitInMilli) {
                                    withContext(Dispatchers.Main) {
                                        showTimeUpDialog(timedApp)
                                    }
                                    timeLimitedApps.remove(packageName)
                                    Timber.d("newLimit: time to show dialog :D")
                                }
                            }

                    }
                    delay(1000)
                }
            }
            Timber.d("End of the loop for the service")
        }
    }

    private fun stopService() {
        Timber.d("Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            Timber.d("Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
    }


    private fun getForegroundAppPackageName(context: Context): String? {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            currentTime - 1900 * 100,
            currentTime
        )
        if (usageStatsList.isEmpty()) {
            return null
        }

        val sortedList = usageStatsList.sortedBy { it.lastTimeUsed }
        val currentPackageName = sortedList.last().packageName

        return currentPackageName
    }


    private fun createNotification(): Notification {
        val notificationChannelId = getString(R.string.time_limit_notification_channel)

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                notificationChannelId,
                getString(R.string.time_limiter_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = getString(R.string.time_limiter_channel_description)
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.importance = NotificationManager.IMPORTANCE_HIGH
                it.vibrationPattern = longArrayOf(100, 200, 300, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }
        val pm = this.packageManager
        val pendingIntent: PendingIntent =
            pm.getLaunchIntentForPackage(packageName).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }

        val builder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                this,
                notificationChannelId
            ) else Notification.Builder(this)

        return builder
            .setContentTitle("TimeLimit Service")
            .setContentText("TimeLimiter background service")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_farhan_transparent)
            .setTicker("Ticker text")
            //.setPriority(Notification.PRIORITY_HIGH)
            .build()
    }

    enum class Actions {
        START,
        STOP
    }

    private val windowManager get() = getSystemService(WINDOW_SERVICE) as WindowManager
    private fun showTimeUpDialog(timeLimit: TimeLimit) {
        /*val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        val composeView = ComposeView(this)
        composeView.setContent {
            var isDialogVisible by remember { mutableStateOf(false) }
            MyDialog(
                onDismissRequest = {isDialogVisible = false}
            ){

            }
        }*/
        /*Dialog(this).apply {
            setContentView(R.layout.timeout_dialog)
            window!!.setType(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            )
            findViewById<Button>(R.id.btn_leave).setOnClickListener {
                context.startActivity(
                    Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME)
                        .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                )
                dismiss()
            }
            findViewById<TextView>(R.id.btn_1min).setOnClickListener {
                timeLimitedApps[timeLimit.packageName] = timeLimit.copy(
                    timeAtAddingInMilli = System.currentTimeMillis(),
                    timeLimitInMilli = 1.seconds.inWholeMilliseconds
                )
                dismiss()
                showToast(R.string.will_remind_you_in_one_min)
            }
            findViewById<TextView>(R.id.btn_3min).setOnClickListener {
                timeLimitedApps[timeLimit.packageName] = timeLimit.copy(
                    timeAtAddingInMilli = System.currentTimeMillis(),
                    timeLimitInMilli = 3.seconds.inWholeMilliseconds
                )
                dismiss()
                showToast(R.string.will_remind_you_in_three_min)
            }
            findViewById<TextView>(R.id.btn_5min).setOnClickListener {
                timeLimitedApps[timeLimit.packageName] = timeLimit.copy(
                    timeAtAddingInMilli = System.currentTimeMillis(),
                    timeLimitInMilli = 5.seconds.inWholeMilliseconds
                )
                dismiss()
                showToast(R.string.will_remind_you_in_five_min)
            }
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            try {
                show()
            } catch (e: Exception) {
                // can't show dialog
                // draw over other apps permission might be revoked!
                e.printStackTrace()
            }
        }*/
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
        val composeView = ComposeView(this)
        composeView.setContent {
            FarhanTheme(darkMode = isDarkMode.isCurrentlyDark(), colorStyle = themeColors) {
                fun dismiss() {
                    windowManager.removeView(composeView)
                }

                val spacing = LocalSpacing.current
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = AlertDialogDefaults.TonalElevation,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(spacing.spaceMedium),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium, Alignment.CenterVertically)
                    ) {
                        Text(stringResource(R.string.time_is_up))
                        Text(stringResource(R.string.or_add_more_time))
                        Button(onClick = {
                            dismiss()
                            this@TimeLimitService.startActivity(
                                Intent(Intent.ACTION_MAIN)
                                    .addCategory(Intent.CATEGORY_HOME)
                                    .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                            )
                        }) {
                            Text(stringResource(R.string.take_me_out_of_here))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceMedium)) {
                            Button(onClick = {
                                timeLimitedApps[timeLimit.packageName] = timeLimit.copy(
                                    timeAtAddingInMilli = System.currentTimeMillis(),
                                    timeLimitInMilli = 1.seconds.inWholeMilliseconds
                                )
                                dismiss()
                                showToast(R.string.will_remind_you_in_five_min)
                            }) { Text(stringResource(R.string.one_min)) }
                            Button(onClick = {
                                timeLimitedApps[timeLimit.packageName] = timeLimit.copy(
                                    timeAtAddingInMilli = System.currentTimeMillis(),
                                    timeLimitInMilli = 3.seconds.inWholeMilliseconds
                                )
                                dismiss()
                                showToast(R.string.will_remind_you_in_five_min)
                            }) { Text(stringResource(R.string.three_min)) }
                            Button(onClick = {
                                timeLimitedApps[timeLimit.packageName] = timeLimit.copy(
                                    timeAtAddingInMilli = System.currentTimeMillis(),
                                    timeLimitInMilli = 5.seconds.inWholeMilliseconds
                                )
                                dismiss()
                                showToast(R.string.will_remind_you_in_five_min)
                            }) { Text(stringResource(R.string.five_min)) }
                        }
                    }
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

    private fun showToast(@StringRes string: Int) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

}