package ly.com.tahaben.launcher_presentation

import android.graphics.Color
import android.widget.TextClock
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.HomeWatcher
import ly.com.tahaben.core.util.SearchEvent
import ly.com.tahaben.core_ui.*
import ly.com.tahaben.launcher_presentation.component.AppListItem
import ly.com.tahaben.launcher_presentation.component.SearchTextFieldLauncher
import timber.log.Timber
import kotlin.math.abs

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class,
    ExperimentalMotionApi::class
)
@Composable
fun LauncherScreen(
    homeWatcher: HomeWatcher,
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val keyboardController = LocalSoftwareKeyboardController.current
    val motionScene = remember {
        context.resources
            .openRawResource(R.raw.launcher_motion_scene)
            .readBytes()
            .decodeToString()
    }
    var componentWidth by remember { mutableStateOf(10000f) }
    val swipeableState = rememberSwipeableState("End")
    val anchors = mapOf(0f to "Start", componentWidth to "End")
    val scope = rememberCoroutineScope()
    val mprogress = (swipeableState.offset.value / componentWidth)
    homeWatcher.setOnHomePressedListener(object : HomeWatcher.OnHomePressedListener {
        override fun onHomePressed() {
            scope.launch {
                swipeableState.animateTo("End")
            }
        }
    })
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
                backgroundColor = White,
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
                val headers = remember {
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
                                viewModel.launchMainActivityForApp(app)
                            },
                                onItemLongClick = {
                                    viewModel.launchAppInfo(app)
                                })
                        }
                    }
                    val offsets = remember { mutableStateMapOf<Int, Float>() }
                    var selectedHeaderIndex by remember { mutableStateOf(0) }
                    val coroutineScope = rememberCoroutineScope()

                    fun updateSelectedIndexIfNeeded(offset: Float) {
                        val index = offsets
                            .mapValues { abs(it.value - offset) }
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

                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(
                                    vertical = spacing.spaceLarge,
                                    horizontal = spacing.spaceExtraSmall
                                )
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        updateSelectedIndexIfNeeded(it.y)
                                    }
                                }
                                .pointerInput(Unit) {
                                    detectVerticalDragGestures { change, _ ->
                                        updateSelectedIndexIfNeeded(change.position.y)
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
                                                offsets[i] = it.boundsInParent().center.y
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
                .layoutId("home_screen"),
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
}