package ly.com.tahaben.farhan.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core_ui.components.CheckboxRow
import ly.com.tahaben.core_ui.components.DropDownTextField
import ly.com.tahaben.core_ui.components.RadioRow
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.onboarding_presentaion.main.MainScreenViewModel
import ly.com.tahaben.usage_overview_domain.util.WorkerKeys
import ly.com.tahaben.usage_overview_presentation.widget.UsageWidget
import ly.com.tahaben.usage_overview_presentation.widget.WidgetWorker
import timber.log.Timber
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 12,Jun,2024
 */

@AndroidEntryPoint
class WidgetConfigActivity : ComponentActivity() {

    @Inject
    lateinit var workManager: WorkManager
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private val configCoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_CANCELED, resultValue)
       configCoroutineScope.launch {
           val oldConfig = getWidgetConfiguration()
           setContent {
               val mainScreenViewModel= hiltViewModel<MainScreenViewModel>()
               val mainState = mainScreenViewModel.mainScreenState.collectAsState().value
               val isDarkMode = when (mainState.uiMode) {
                   UIModeAppearance.DARK_MODE -> true
                   UIModeAppearance.LIGHT_MODE -> false
                   UIModeAppearance.FOLLOW_SYSTEM -> isSystemInDarkTheme()
               }
               FarhanTheme(
                   colorStyle = mainState.themeColors,
                   darkMode = isDarkMode
               ) {
                   Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                       WidgetConfigScreen(
                           modifier = Modifier
                               .fillMaxSize()
                               .padding(innerPadding),
                           oldConfig = oldConfig,
                           saveConfig = ::updateConfiguration
                       )
                   }
               }
           }
       }

    }
    private fun updateConfiguration(state: WidgetPreviewState){


        val glanceId = GlanceAppWidgetManager(this).getGlanceIdBy(appWidgetId)
        configCoroutineScope.launch {
            UsageWidget.apply {
                updateAppWidgetState(this@WidgetConfigActivity, glanceId){prefs ->
                    prefs[UsageWidget.themeColors] = state.themeColors.name
                    prefs[UsageWidget.uiMode] = state.uiModeAppearance.name
                    prefs[UsageWidget.style] = state.style.name
                    prefs[UsageWidget.vsYesterday] = state.compareWithYesterday
                    prefs[textSize] = state.textSize
                }
            }
            UsageWidget.update(this@WidgetConfigActivity, glanceId)
        }

        val duration = state.updateInterval.minutes
        val request = PeriodicWorkRequestBuilder<WidgetWorker>(duration.toJavaDuration())
            .build()

        val operation = workManager.enqueueUniquePeriodicWork(
            WorkerKeys.WIDGET_UPDATE,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )


        Timber.d("widget work request: ${operation.result}")
        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
    private suspend fun getWidgetConfiguration(): WidgetPreviewState? {
        val glanceId = GlanceAppWidgetManager(this).getGlanceIdBy(appWidgetId)
        //var oldState: WidgetPreviewState? = null
        val state: MutablePreferences = UsageWidget.getAppWidgetState(this@WidgetConfigActivity, glanceId)
        val oldConfig = WidgetPreviewState(
            themeColors = ThemeColors.valueOf(state[UsageWidget.themeColors] ?: ThemeColors.Classic.name),
            uiModeAppearance = UIModeAppearance.valueOf(state[UsageWidget.uiMode] ?: UIModeAppearance.FOLLOW_SYSTEM.name),
            style = UsageWidget.Style.valueOf(state[UsageWidget.style] ?: UsageWidget.Style.VERTICAL.name),
            compareWithYesterday = state[UsageWidget.vsYesterday] ?: false,
            textSize = state[UsageWidget.textSize] ?: 32
        )
        return oldConfig
    }
}

@Composable
fun WidgetConfigScreen(modifier: Modifier = Modifier,oldConfig: WidgetPreviewState? = null, saveConfig: (WidgetPreviewState)-> Unit) {

    var state by remember {
        mutableStateOf<WidgetPreviewState>(oldConfig ?: WidgetPreviewState())
    }
    val decimalFormat = DecimalFormat.getInstance()
    val isDarkMode = when (state.uiModeAppearance) {
        UIModeAppearance.DARK_MODE -> true
        UIModeAppearance.LIGHT_MODE -> false
        UIModeAppearance.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }
    val periodsMapList = listOf(
        15 to "15 min",
        30 to "30 min",
        60 to "1 hour",
        120 to "2 hours",
        180 to "3 hours",
        360 to "6 hours",
        720 to "12 hours"
    )
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
Spacer(modifier = Modifier.height(32.dp))
        Text(text = stringResource(id = R.string.customize_your_widget))
        Spacer(modifier = Modifier.height(8.dp))
        FarhanTheme(darkMode = isDarkMode, colorStyle = state.themeColors) {
            // Preview
            Box(modifier = Modifier) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp), horizontalAlignment = CenterHorizontally
                ) {
                    Row(modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UsageColumn(
                            style = state.style.name,
                            hours = 4,
                            minutes = 17,
                            textSize = state.textSize,
                            header = R.string.usage_for_today
                        )
                        if (state.compareWithYesterday) {
                            VerticalDivider(
                                Modifier.height(100.dp),
                                color = MaterialTheme.colorScheme.inversePrimary
                            )
                            UsageColumn(
                                style = state.style.name,
                                hours = 3,
                                minutes = 17,
                                textSize = state.textSize,
                                header = R.string.yesterday
                            )
                        }
                    }
                }
                Image(
                    modifier = Modifier.align(TopEnd).padding(end = 16.dp, top = 16.dp).size(16.dp),
                    painter = painterResource(R.drawable.baseline_refresh_24),
                    contentDescription = "refresh",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
        } // end of preview
        Spacer(modifier = Modifier.height(32.dp))
        var lightTopLeft by remember {
            mutableStateOf(Offset(0f, 0f))
        }
        var darkTopLeft by remember {
            mutableStateOf(Offset(0f, 0f))
        }
        var followTopLeft by remember {
            mutableStateOf(Offset(0f, 0f))
        }
        val animatedOptionTopLeft by
        animateOffsetAsState(
            targetValue = when (state.uiModeAppearance) {
                UIModeAppearance.DARK_MODE -> darkTopLeft.plus(Offset(65f, 65f))
                UIModeAppearance.LIGHT_MODE -> lightTopLeft.plus(Offset(65f, 65f))
                UIModeAppearance.FOLLOW_SYSTEM -> followTopLeft.plus(Offset(65f, 65f))
            }, label = "selected option"
        )
        val rowBackgroundColor by animateColorAsState(
            targetValue = MaterialTheme.colorScheme.primary,
            label = "ui mode row background"
        )
        Row(
            modifier = Modifier
                .drawBehind {
                    drawRoundRect(
                        color = rowBackgroundColor,
                        size = size,
                        cornerRadius = CornerRadius(65f)
                    )
                    drawCircle(color = Color.White, radius = 50f, center = animatedOptionTopLeft)
                }
        ) {

            IconButton(
                modifier = Modifier.onGloballyPositioned {
                    darkTopLeft = it.positionInParent()
                },
                onClick = {
                    state = state.copy(uiModeAppearance = UIModeAppearance.DARK_MODE)
                }) {
                Icon(imageVector = Icons.Filled.DarkMode, contentDescription = "")
            }

            IconButton(
                modifier = Modifier.onGloballyPositioned {
                    followTopLeft = it.positionInParent()
                },
                onClick = {
                    state = state.copy(uiModeAppearance = UIModeAppearance.FOLLOW_SYSTEM)
                }) {
                Icon(imageVector = Icons.Filled.BrightnessAuto, contentDescription = "")
            }
            IconButton(
                modifier = Modifier.onGloballyPositioned {
                    lightTopLeft = it.positionInParent()
                },
                onClick = {
                    state = state.copy(uiModeAppearance = UIModeAppearance.LIGHT_MODE)
                }) {
                Icon(imageVector = Icons.Filled.LightMode, contentDescription = "")
            }
        }
        val activeModeName = when (state.uiModeAppearance) {
            UIModeAppearance.DARK_MODE -> stringResource(id = R.string.dark)
            UIModeAppearance.LIGHT_MODE -> stringResource(id = R.string.light)
            UIModeAppearance.FOLLOW_SYSTEM -> stringResource(id = R.string.follow_system)
        }

        Crossfade(targetState = activeModeName, label = "ui mode name") { text ->
            Text(modifier = Modifier.fillMaxWidth(), text = text, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RadioRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, string = stringResource(id = R.string.classic), selected = state.themeColors == ThemeColors.Classic, onClick = {state = state.copy(themeColors = ThemeColors.Classic)})
            RadioRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, string = stringResource(id = R.string.vibrant), selected = state.themeColors == ThemeColors.Vibrant, onClick = {state = state.copy(themeColors = ThemeColors.Vibrant)})
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                RadioRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, string = stringResource(id = R.string.dynamic), selected = state.themeColors == ThemeColors.Dynamic, onClick = {state = state.copy(themeColors = ThemeColors.Dynamic)})
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RadioRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically,string = stringResource(id = R.string.vertical), selected = state.style == UsageWidget.Style.VERTICAL, onClick = {state = state.copy(style = UsageWidget.Style.VERTICAL)})
            RadioRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically,string = stringResource(id = R.string.horizontal), selected = state.style == UsageWidget.Style.HORIZONTAL, onClick = {state = state.copy(style = UsageWidget.Style.HORIZONTAL)})
        }
        Spacer(Modifier.height(16.dp))
        CheckboxRow(string = stringResource(id = R.string.compare_with_yesterday), selected = state.compareWithYesterday, onCheckedChange = {state = state.copy(compareWithYesterday = it)})
        Spacer(Modifier.height(8.dp))

        Text(text = stringResource(id = R.string.widget_update_interval_text))
        DropDownTextField(
            menuExpanded = state.isTextSizeMenuExpanded,
            onExpandedChanged = { state = state.copy(isTextSizeMenuExpanded = it)},
            text = state.textSize.toString(),
            onTextChange = {},
            readOnly = true,
            label = stringResource(id = R.string.text_size),
            menuContent = {
                (8..48 step 2).forEach { value ->
                    DropdownMenuItem(
                        text = { Text(text = value.toString()) },
                        onClick = {
                            state = state.copy(
                            isTextSizeMenuExpanded = false,
                            textSize = value
                            )
                        }
                    )
                }
            }
        )
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.widget_update_interval_text))
        DropDownTextField(
            menuExpanded = state.isMenuExpanded,
            onExpandedChanged = { state = state.copy(isMenuExpanded = it)},
            text = state.updateInterval.toString(),
            onTextChange = {},
            readOnly = true,
            label = stringResource(id = R.string.minute),
            menuContent = {
                periodsMapList.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(text = value) },
                        onClick = {
                            state = state.copy(
                            isMenuExpanded = false,
                            updateInterval = key
                            )
                        }
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            saveConfig(state)
        }) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}

@Composable
private fun UsageTextWidget(
    style: String?,
    todayHours: Long,
    todayMinutes: Int,
    textSize: Int
) {
    val decimalFormat = DecimalFormat.getInstance()
    when (style) {
        UsageWidget.Style.HORIZONTAL.name -> {
            Text(
                text = decimalFormat.format(todayHours) + stringResource(R.string.hours) + " " +
                        decimalFormat.format(todayMinutes) + stringResource(R.string.minutes),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = textSize.sp
                )
            )
        }

        UsageWidget.Style.VERTICAL.name -> {
            Text(
                text = decimalFormat.format(todayHours) + stringResource(R.string.hours) + "\n" +
                        decimalFormat.format(todayMinutes) +  stringResource(R.string.minutes),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = textSize.sp
                )
            )
        }

        else -> {
            Text(
                text = decimalFormat.format(todayHours) + stringResource(R.string.hours) + " " +
                        decimalFormat.format(todayMinutes) +  stringResource(R.string.minutes),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = textSize.sp
                )
            )
        }
    }
}

@Composable
private fun UsageColumn(
    style: String?,
    hours: Long,
    minutes: Int,
    textSize: Int,
    @StringRes header:  Int,
) {
    Column(
        Modifier.width(120.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(header),
            style = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)
        )
        Spacer(Modifier.height(8.dp))
        UsageTextWidget(
            style,
            hours,
            minutes,
            textSize
        )
    }
}

@PreviewLightDark
@Composable
fun WidgetConfigScreenPreview() {
    FarhanTheme(
        colorStyle = ThemeColors.Classic,
        darkMode = false
    ) {
        WidgetConfigScreen(saveConfig = { _->})
    }
}