package ly.com.tahaben.launcher_presentation.launcher

import android.app.Activity
import android.graphics.Color
import android.widget.TextClock
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.HomeWatcher
import ly.com.tahaben.core.util.SearchEvent
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.OnLifecycleEvent
import ly.com.tahaben.core_ui.components.DelayedLaunchOverlay
import ly.com.tahaben.core_ui.components.MyDialog
import ly.com.tahaben.launcher_presentation.component.AppListItem
import ly.com.tahaben.launcher_presentation.component.SearchTextFieldLauncher
import timber.log.Timber

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMotionApi::class, ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun LauncherScreen(
    homeWatcher: HomeWatcher,
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val resources = LocalResources.current
    val state = viewModel.state
    val keyboardController = LocalSoftwareKeyboardController.current
    val motionScene = remember {
        resources
            .openRawResource(R.raw.launcher_motion_scene)
            .readBytes()
            .decodeToString()
    }
    var componentWidth by remember { mutableStateOf(10000f) }
    val swipeableState = rememberSwipeableState("End")
    val anchors = mapOf(0f to "Start", componentWidth to "End")
    val scope = rememberCoroutineScope()
    val mprogress = (swipeableState.offset.value / componentWidth)
    val view = LocalView.current
    val isBackgroundLight = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val isAppsListVisible = mprogress < 0.5f
    if (!view.isInEditMode) {
        LaunchedEffect(isAppsListVisible, isBackgroundLight) {
            val window = (view.context as? Activity)?.window ?: return@LaunchedEffect
            // Over the wallpaper (home) keep light icons, over the apps list match the theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                isAppsListVisible && isBackgroundLight
        }
    }
    homeWatcher.setOnHomePressedListener(object : HomeWatcher.OnHomePressedListener {
        override fun onHomePressed() {
            scope.launch {
                swipeableState.animateTo("End")
            }
        }
    })
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                Timber.d("refresh apps")
                viewModel.refreshApps()
            }

            else -> Unit
        }
    }
    MotionLayout(
        motionScene = MotionScene(content = motionScene),
        progress = mprogress,
        modifier = Modifier
            .fillMaxSize()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                reverseDirection = false,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .onSizeChanged { size ->
                componentWidth = size.width.toFloat()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .layoutId("apps_list")
        ) {
            TopAppBar(
                title = {},
//                backgroundColor = White,
                actions = {
                    SearchTextFieldLauncher(
                        text = state.query,
                        onValueChange = {
                            viewModel.onEvent(SearchEvent.OnQueryChange(it))
                        },
                        shouldShowHint = state.isHintVisible,
                        onClearSearch = {
                            keyboardController?.hide()
                            viewModel.onEvent(SearchEvent.HideSearch)
                        },
                        onSearchPressed = {
                            keyboardController?.hide()
                        },
                        onFocusChanged = {
                            viewModel.onEvent(SearchEvent.OnSearchFocusChange(it.isFocused))
                        }
                    )
                }
            )
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val headers = remember(state.appsList) {
                    state.appsList.map { it.name?.first()?.uppercase() }.toSet().toList()
                }
                Row {
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f)
                    ) {
                        items(state.searchResults) { app ->
                            AppListItem(app = app, onItemClick = {
                                viewModel.onAppClick(app)
                            },
                                onItemLongClick = {
                                    viewModel.launchAppInfo(app)
                                })
                        }
                    }
                    val offsets = remember { mutableStateMapOf<Int, Offset>() }
                    var selectedHeaderIndex by remember { mutableStateOf(0) }
                    val coroutineScope = rememberCoroutineScope()

                    fun updateSelectedIndexIfNeeded(position: Offset) {
                        val index = offsets
                            .mapValues { (it.value - position).getDistanceSquared() }
                            .entries
                            .minByOrNull { it.value }
                            ?.key ?: return
                        if (selectedHeaderIndex == index) return
                        selectedHeaderIndex = index
                        val selectedItemIndex = state.appsList.indexOfFirst {
                            it.name?.first()?.uppercase() == headers[selectedHeaderIndex]
                        }
                        coroutineScope.launch {
                            listState.animateScrollToItem(selectedItemIndex)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(
                                vertical = spacing.spaceLarge,
                            )
                            .clip(
                                RoundedCornerShape(20.dp, 0.dp, 0.dp, 20.dp)
                            )
                            .background(color = LightGray),
                    ) {

                        FlowColumn(
                            verticalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(
                                    vertical = spacing.spaceLarge,
                                    horizontal = spacing.spaceExtraSmall
                                )
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        updateSelectedIndexIfNeeded(it)
                                    }
                                }
                                .pointerInput(Unit) {
                                    detectVerticalDragGestures { change, _ ->
                                        updateSelectedIndexIfNeeded(change.position)
                                    }
                                }
                        ) {
                            headers.forEachIndexed { i, header ->
                                if (header != null) {
                                    Text(
                                        header,
                                        modifier = Modifier
                                            .padding(horizontal = spacing.spaceExtraSmall)
                                            .onGloballyPositioned {
                                                offsets[i] = it.boundsInParent().center
                                            },
                                        color = White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .layoutId("home_screen")
                .safeContentPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                AndroidView(
                    modifier = Modifier
                        .padding(top = spacing.spaceLarge)
                        .align(Alignment.BottomCenter)
                        .clickable {
                            viewModel.launchDefaultAlarmApp()
                        },
                    factory = { context ->
                        val tc = TextClock(context)
                        tc.textSize = 46f
                        tc.setTextColor(Color.WHITE)
                        tc.format12Hour.let { tc.format12Hour = "hh:mm:ss a" }
                        tc
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.spaceSmall),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = viewModel::launchDefaultDialerApp
                ) {
                    Icon(
                        modifier = Modifier
                            .size(46.dp),
                        imageVector = Icons.Filled.Phone,
                        contentDescription = stringResource(R.string.dialer),
                        tint = White
                    )
                }
                IconButton(
                    onClick = viewModel::launchDefaultCameraApp
                ) {
                    Icon(
                        modifier = Modifier
                            .size(46.dp),
                        imageVector = Icons.Filled.Camera,
                        contentDescription = stringResource(R.string.camera),
                        tint = White
                    )
                }
            }
        }
    }
    if (state.isTimeLimitDialogVisible) {
        MyDialog(
            onDismissRequest = { viewModel.dismissTimeLimitDialog() },
        ) {
            Text(
                text = stringResource(
                    R.string.set_time_limite_dialog_text,
                    state.timeLimitedApp?.name.toString()
                ),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    state.timeLimitedApp?.let { app ->
                        viewModel.setTimeLimitAndLunchApp(
                            app = app,
                            timeLimitInMinutes = 5
                        )
                    }
                }) {
                    Text(
                        text = stringResource(id = R.string.five_min),
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Button(onClick = {
                    state.timeLimitedApp?.let { app ->
                        viewModel.setTimeLimitAndLunchApp(
                            app = app,
                            timeLimitInMinutes = 10
                        )
                    }
                }) {
                    Text(
                        text = stringResource(R.string.ten_min),
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Button(onClick = {
                    state.timeLimitedApp?.let { app ->
                        viewModel.setTimeLimitAndLunchApp(
                            app = app,
                            timeLimitInMinutes = 15
                        )
                    }
                }) {
                    Text(
                        text = stringResource(R.string.fifteen_min),
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

        }
    }
    if (state.isDelayRunning) {
        DelayedLaunchOverlay(
            delayInSeconds = state.delayDurationSeconds,
            launchCount = state.launchAttemptCount,
            appName = state.timeLimitedApp?.name.orEmpty(),
            delayMessage = state.delayMessage,
            openApp = {
                viewModel.disableOverlay()
                state.timeLimitedApp?.let { viewModel.launchActivityForApp(it) }
            },
            dismissOverlay = { viewModel.disableOverlay() })
    }
}