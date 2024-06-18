package ly.com.tahaben.usage_overview_presentation.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.color.ColorProviders
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core.navigation.Routes
import ly.com.tahaben.core_ui.theme.classicDarkScheme
import ly.com.tahaben.core_ui.theme.classicLightScheme
import ly.com.tahaben.core_ui.theme.darkScheme
import ly.com.tahaben.core_ui.theme.lightScheme
import ly.com.tahaben.usage_overview_domain.preferences.Preferences
import ly.com.tahaben.usage_overview_domain.util.WorkerKeys
import timber.log.Timber
import java.text.DecimalFormat
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 12,Jun,2024
 */
object UsageWidget : GlanceAppWidget() {
    val todayTimeKey = longPreferencesKey("today_time_today")
    private val yesterdayTimeKey = longPreferencesKey("yesterday_time_today")
    val themeColors = stringPreferencesKey("widget_theme_colors")
    val uiMode = stringPreferencesKey("widget_ui_mode")
    val style = stringPreferencesKey("widget_style")
    val vsYesterday = booleanPreferencesKey("widget_compare_yesterday")
    val textSize = intPreferencesKey("text_size")
    private val isLoadingKey = booleanPreferencesKey("is_loading")

    enum class Style {
        HORIZONTAL, VERTICAL
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val preferences = getPreferences(context)
        updateAppWidgetState(context, id) { prefs ->
            prefs[todayTimeKey] = preferences.getTodayUsage()
            prefs[yesterdayTimeKey] = preferences.getYesterdayUsage()
        }
        val widgetCoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        Timber.d("updated app widget state $")
        provideContent {
            val isLoading = currentState<Boolean>(isLoadingKey) == true
            Timber.d("provide content")
            val today = LocalDate.now()
            GlanceTheme(colors = getThemeColors(context)) {
                val totalTime = currentState<Long>(todayTimeKey) ?: preferences.getTodayUsage()
                val yesterdayDuration =
                    currentState<Long>(yesterdayTimeKey) ?: preferences.getYesterdayUsage()
                val textSize = currentState<Int>(textSize) ?: 32
                val uiMode = currentState<String>(uiMode)

                val (todayHours, todayMinutes) = totalTime.milliseconds.toComponents { hours, minutes, _, _ ->
                    hours to minutes
                }
                val (yesterdayHours, yesterdayMinutes) = yesterdayDuration.milliseconds.toComponents { hours, minutes, _, _ ->
                    hours to minutes
                }
                val style = currentState<String>(style)
                val compareWithYesterday = currentState<Boolean>(vsYesterday) ?: false
                Timber.d("hours $todayHours minutes $todayMinutes")
                Box(modifier = GlanceModifier, contentAlignment = Alignment.TopEnd) {
                    Box(modifier = GlanceModifier, contentAlignment = Alignment.TopStart) {
                        Image(
                            modifier = GlanceModifier.fillMaxSize(),
                            provider = ImageProvider(R.drawable.widget_bg),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.primaryContainer)
                        )
                        Column(
                            modifier = GlanceModifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Vertical.CenterVertically
                        ) {
                            Spacer(GlanceModifier.height(8.dp))
                            Column(
                                modifier = GlanceModifier.fillMaxSize(),
                                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                            ) {
                                if (isLoading) {
                                    Text(
                                        text = context.getString(R.string.loading),
                                        style = TextStyle(color = GlanceTheme.colors.onBackground)
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.Vertical.Bottom,
                                        horizontalAlignment = Alignment.Horizontal.End
                                    ) {
                                        UsageColumn(
                                            context,
                                            style,
                                            todayHours,
                                            todayMinutes,
                                            textSize,
                                            R.string.usage_for_today,
                                            today.toString()
                                        )
                                        if (compareWithYesterday) {
                                            Spacer(GlanceModifier.width(8.dp))
                                            // add divider
                                            Image(
                                                modifier = GlanceModifier.width(0.5.dp),
                                                provider = ImageProvider(R.drawable.vertical_divider),
                                                contentDescription = null,
                                                colorFilter = ColorFilter.tint(GlanceTheme.colors.inversePrimary)
                                            )
                                            Spacer(GlanceModifier.width(8.dp))
                                            UsageColumn(
                                                context = context,
                                                style = style,
                                                hours = yesterdayHours,
                                                minutes = yesterdayMinutes,
                                                textSize = textSize,
                                                header = R.string.yesterday,
                                                today.minusDays(1).toString()
                                            )
                                        }
                                    }
                                }
                                Spacer(GlanceModifier.height(8.dp))
                                if (isLoading) {
                                    CircularProgressIndicator(color = GlanceTheme.colors.tertiary)
                                }

                            }

                        }
                        Image(
                            modifier = GlanceModifier.size(25.dp),
                            provider = ImageProvider(R.drawable.farhan_transparent_bg),
                            contentDescription = null,
                            // If light theme add darker tint for some contrast with background
                            colorFilter = if (uiMode == UIModeAppearance.LIGHT_MODE.name) {
                                ColorFilter.tint(GlanceTheme.colors.onPrimaryContainer)
                            } else {
                                null
                            }
                        )
                    }
                    Image(
                        modifier = GlanceModifier.padding(end = 16.dp, top = 16.dp).size(32.dp)
                            .clickable {
                                if (isLoading) {
                                    Timber.d("is Loading true")
                                    return@clickable
                                }
                                widgetCoroutineScope.launch {
                                    updateAppWidgetState(context, id){prefs ->
                                        prefs[isLoadingKey] = true
                                    }
                                    update(context, id)
                                    refresh(context, id)
                                }
                            },
                        provider = ImageProvider(R.drawable.baseline_refresh_24),
                        contentDescription = "refresh",
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground)
                    )
                }
            }
        }
    }


    @Composable
    private fun UsageColumn(
        context: Context,
        style: String?,
        hours: Long,
        minutes: Int,
        textSize: Int,
        @StringRes header: Int,
        date: String
    ) {
        Column(
            GlanceModifier.width(120.dp).clickable {
                openFarhanAndNavigateToUsageScreen(context, date)
            },
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = context.getString(header),
                style = TextStyle(color = GlanceTheme.colors.onPrimaryContainer)
            )
            Spacer(GlanceModifier.height(8.dp))
            UsageTextWidget(
                style,
                hours,
                context,
                minutes,
                textSize
            )
        }
    }

    @Composable
    private fun UsageTextWidget(
        style: String?,
        todayHours: Long,
        context: Context,
        todayMinutes: Int,
        textSize: Int
    ) {
        val decimalFormat = DecimalFormat.getInstance()
        when (style) {
            Style.HORIZONTAL.name -> {
                Text(
                    text = decimalFormat.format(todayHours) + context.getString(R.string.hours) + " " +
                            decimalFormat.format(todayMinutes) + context.getString(R.string.minutes),
                    style = TextStyle(
                        color = GlanceTheme.colors.secondary,
                        fontSize = textSize.sp
                    )
                )
            }

            Style.VERTICAL.name -> {
                Text(
                    text = decimalFormat.format(todayHours) + context.getString(R.string.hours) + "\n" +
                            decimalFormat.format(todayMinutes) + context.getString(R.string.minutes),
                    style = TextStyle(
                        color = GlanceTheme.colors.secondary,
                        fontSize = textSize.sp
                    )
                )
            }

            else -> {
                Text(
                    text = decimalFormat.format(todayHours) + context.getString(R.string.hours) + " " +
                            decimalFormat.format(todayMinutes) + context.getString(R.string.minutes),
                    style = TextStyle(
                        color = GlanceTheme.colors.secondary,
                        fontSize = textSize.sp
                    )
                )
            }
        }
    }


    @Composable
    private fun getThemeColors(context: Context): ColorProviders {
        val selectedTheme = currentState<String>(themeColors)
        val darkMode = currentState<String>(uiMode)
        return when (selectedTheme) {
            ThemeColors.Classic.name -> {
                when (darkMode) {
                    UIModeAppearance.DARK_MODE.name -> ClassicColorScheme.dark
                    UIModeAppearance.LIGHT_MODE.name -> ClassicColorScheme.light
                    else -> ClassicColorScheme.followSystem
                }
            }

            ThemeColors.Vibrant.name -> {
                when (darkMode) {
                    UIModeAppearance.DARK_MODE.name -> VibrantColorScheme.dark
                    UIModeAppearance.LIGHT_MODE.name -> VibrantColorScheme.light
                    else -> VibrantColorScheme.followSystem
                }
            }

            ThemeColors.Dynamic.name -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    when (darkMode) {
                        UIModeAppearance.DARK_MODE.name -> DynamicColorScheme(context).dark
                        UIModeAppearance.LIGHT_MODE.name -> DynamicColorScheme(context).light
                        else -> DynamicColorScheme(context).followSystem
                    }
                } else {
                    throw Exception("Dynamic option is only available on Android 12+")
                }
            }

            else -> ClassicColorScheme.followSystem
        }
    }

    private suspend fun refresh(context: Context, glanceId: GlanceId) {
        val wm = getWorkManager(context)
        val request = OneTimeWorkRequestBuilder<WidgetWorker>()
            .build()
        wm.enqueueUniqueWork("refresh", ExistingWorkPolicy.REPLACE, request)
        wm.getWorkInfosForUniqueWorkFlow("refresh").collect{workInfos ->
            for (workInfo in workInfos){
                Timber.d("work info $workInfo state: ${workInfo.state}")
                if (workInfo.state.isFinished){
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[isLoadingKey] = false
                    }
                    update(context,glanceId)
                }
            }
        }
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        Timber.d("onDelete Widget")
        super.onDelete(context, glanceId)
        val widgetListEmpty =
            GlanceAppWidgetManager(context).getGlanceIds(UsageWidget::class.java).isEmpty()
        if (widgetListEmpty) {
            cancelExistingWorkers(context)
        }
    }
}

@AndroidEntryPoint
class WidgetBroadcast() : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = UsageWidget
}


@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    val preferences: Preferences
    val workManager: WorkManager
}

private fun getPreferences(context: Context): Preferences {
    val hiltEntryPoint = EntryPointAccessors.fromApplication(
        context, WidgetEntryPoint::class.java
    )
    return hiltEntryPoint.preferences
}

private fun getWorkManager(context: Context): WorkManager {
    val hiltEntryPoint = EntryPointAccessors.fromApplication(
        context, WidgetEntryPoint::class.java
    )
    return hiltEntryPoint.workManager
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[UsageWidget.todayTimeKey] = -1L
        }
        UsageWidget.update(context, glanceId)
        delay(2000)
        Timber.d("refresh action call")
        val wm = getWorkManager(context)
        val request = OneTimeWorkRequestBuilder<WidgetWorker>()
            .build()
        wm.enqueueUniqueWork("refresh", ExistingWorkPolicy.REPLACE, request)
    }
}

private fun openFarhanAndNavigateToUsageScreen(context: Context, date: String) {
    Timber.d("openFarhanAndNavigateToUsageScreen: $date")
    val deepLinkUri =
        Uri.parse("app://${context.packageName}/${Routes.USAGE}/$date")
    val pm = context.packageManager
    val showUsageIntent = pm.getLaunchIntentForPackage(context.packageName)?.apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        action = Intent.ACTION_VIEW
        data = deepLinkUri
    }
    context.startActivity(showUsageIntent)
}

private fun cancelExistingWorkers(context: Context) {
    val wm = getWorkManager(context)
    wm.cancelUniqueWork(WorkerKeys.WIDGET_UPDATE)
}

object VibrantColorScheme {
    val light = ColorProviders(
        light = lightScheme,
        dark = lightScheme
    )
    val dark = ColorProviders(
        light = darkScheme,
        dark = darkScheme
    )
    val followSystem = ColorProviders(
        light = lightScheme,
        dark = darkScheme
    )
}

object ClassicColorScheme {
    val light = ColorProviders(
        light = classicLightScheme,
        dark = classicLightScheme
    )
    val dark = ColorProviders(
        light = classicDarkScheme,
        dark = classicDarkScheme
    )
    val followSystem = ColorProviders(
        light = classicLightScheme,
        dark = classicDarkScheme
    )
}

@RequiresApi(Build.VERSION_CODES.S)
class DynamicColorScheme(context: Context) {
    val light = ColorProviders(
        light = dynamicLightColorScheme(context),
        dark = dynamicLightColorScheme(context)
    )
    val dark = ColorProviders(
        light = dynamicDarkColorScheme(context),
        dark = dynamicDarkColorScheme(context)
    )
    val followSystem = ColorProviders(
        light = dynamicLightColorScheme(context),
        dark = dynamicDarkColorScheme(context)
    )
}