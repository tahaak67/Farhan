package ly.com.tahaben.infinite_scroll_blocker_presentation

import android.widget.NumberPicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.*
import timber.log.Timber

@Composable
fun InfiniteScrollingBlockerScreen(
    onNavigateUp: () -> Unit,
    onNavigateToExceptions: () -> Unit,
    viewModel: InfiniteScrollBlockerViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val isServiceChecked = remember { mutableStateOf(state.isServiceEnabled) }
    val timeOutDuration = remember { mutableStateOf(state.timeoutDuration) }
    val openDialog = remember { mutableStateOf(false) }
    val openHowDialog = remember { mutableStateOf(false) }
    val openHowAccessibilityDialog = remember { mutableStateOf(false) }
    isServiceChecked.value = state.isServiceEnabled

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.checkServiceStats()
                viewModel.checkIfAppearOnTopPermissionGranted()
            }
            else -> Unit
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.infinite_scroll_blocker_settings))
            },
            backgroundColor = White,
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        modifier = Modifier.mirror(),
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        )
        if (!state.isAppearOnTopPermissionGranted) {
            PermissionNotGrantedContent(
                modifier = Modifier.fillMaxSize(),
                message = stringResource(R.string.appear_on_top_permission_not_granted),
                subMessage = stringResource(R.string.appear_on_top_permission_needed_message),
                onGrantClick = viewModel::askForAppearOnTopPermission,
                onHowClick = { openHowDialog.value = true }
            )

        } else if (!state.isAccessibilityPermissionGranted) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.one_more_thing),
                    style = MaterialTheme.typography.h3,
                    textAlign = TextAlign.Center
                )
                PermissionNotGrantedContent(
                    modifier = Modifier,
                    message = stringResource(R.string.accessibility_permission_not_granted),
                    subMessage = stringResource(R.string.accessibility_permission_needed_message),
                    onGrantClick = viewModel::askForAccessibilityPermission,
                    onHowClick = { openHowAccessibilityDialog.value = true }
                )
            }
        } else {
            Column(
                Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.spaceExtraLarge)
                        .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmall),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Text(text = stringResource(R.string.infinite_scroll_blocker))
                    Switch(
                        checked = isServiceChecked.value,
                        onCheckedChange = { checked ->
                            viewModel.setServiceStats(checked)
                            isServiceChecked.value = checked
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.spaceExtraLarge)
                        .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmall)
                        .clickable {
                            onNavigateToExceptions()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.exceptions),
                        textAlign = TextAlign.Justify
                    )
                }
                AnimatedVisibility(visible = true) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = spacing.spaceMedium,
                                vertical = spacing.spaceSmall
                            )
                            .clickable {
                                openDialog.value = true
                            }
                    ) {

                        Text(text = state.timeoutDuration.toString() + stringResource(id = R.string.minutes))
                        Text(text = stringResource(R.string.tap_to_set_new_time))
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        Dialog(
            onDismissRequest = { openDialog.value = false },
            properties = DialogProperties(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Text(
                    text = stringResource(R.string.remind_me_to_stop_scrolling_after),
                    style = MaterialTheme.typography.h4
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                val npVal = remember {
                    mutableStateOf(state.timeoutDuration)
                }
                Row {
                    AndroidView(factory = { context ->
                        val np = NumberPicker(context)
                        np.value = timeOutDuration.value
                        np.maxValue = 60
                        np.minValue = 1
                        np.setOnValueChangedListener { _, i, i2 ->
                            Timber.d("np: oldv: $i newv: $i2")
                            npVal.value = i2
                        }
                        np
                    })
                    Text(
                        modifier = Modifier
                            .padding(horizontal = spacing.spaceExtraSmall)
                            .align(Alignment.CenterVertically),
                        text = stringResource(id = R.string.minutes),
                        style = MaterialTheme.typography.h4
                    )
                }
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                        }
                    ) {
                        Text(
                            stringResource(R.string.dismiss),
                            style = MaterialTheme.typography.h5
                        )
                    }
                    TextButton(
                        onClick = {
                            openDialog.value = false
                            Timber.d("setting as new timeout ${npVal.value}")
                            viewModel.setTimeoutDuration(npVal.value)
                        }
                    ) {
                        Text(
                            stringResource(id = R.string.confirm),
                            style = MaterialTheme.typography.h5
                        )
                    }
                }
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
            }
        }
    }
    if (openHowDialog.value) {
        HowDialog(
            gifId = R.drawable.appear_on_top_permission_howto,
            gifDescription = stringResource(R.string.how_to_enable_permission),
            onDismiss = { openHowDialog.value = false }
        )
    }
    if (openHowAccessibilityDialog.value) {
        HowDialog(
            gifId = R.drawable.accessibility_permission_howto,
            gifDescription = stringResource(R.string.how_to_enable_permission),
            onDismiss = { openHowAccessibilityDialog.value = false }
        )
    }
}
