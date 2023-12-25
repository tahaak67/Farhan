package ly.com.tahaben.infinite_scroll_blocker_presentation

import android.os.Build
import android.widget.NumberPicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.OnLifecycleEvent
import ly.com.tahaben.core_ui.components.AccessibilityNotRunningContent
import ly.com.tahaben.core_ui.components.HowDialog
import ly.com.tahaben.core_ui.components.PermissionNotGrantedContent
import ly.com.tahaben.core_ui.components.getAnnotatedStringBulletList
import ly.com.tahaben.core_ui.mirror
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfiniteScrollingBlockerScreen(
    onNavigateUp: () -> Unit,
    onNavigateToExceptions: () -> Unit,
    viewModel: InfiniteScrollBlockerViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val openDialog = remember { mutableStateOf(false) }
    val openHowDialog = remember { mutableStateOf(false) }
    val openHowAccessibilityDialog = remember { mutableStateOf(false) }

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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
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
            val messages = listOf(
                stringResource(R.string.reason_know_if_app_in_exceptions),
                stringResource(R.string.reason_know_how_much_you_scroll),
                stringResource(R.string.reason_display_warning_if_infinite_scroll_detected)
            )
            val permissionReasons =
                getAnnotatedStringBulletList(messages)

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.one_more_thing),
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center
                )
                AccessibilityNotRunningContent(
                    modifier = Modifier,
                    message = stringResource(R.string.accessibility_permission_not_granted),
                    subMessage = stringResource(R.string.accessibility_permission_needed_message),
                    permissionReasons = permissionReasons,
                    onGrantClick = viewModel::askForAccessibilityPermission,
                    onBack = onNavigateUp,
                    onHowClick = { openHowAccessibilityDialog.value = true }
                )
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.spaceMedium)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.spaceMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Text(text = stringResource(R.string.infinite_scroll_blocker))
                    Switch(
                        checked = state.isServiceEnabled,
                        onCheckedChange = { checked ->
                            viewModel.setServiceStats(checked)
                        }
                    )
                }
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.spaceMedium)
                        .clickable {
                            onNavigateToExceptions()
                        },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.exceptions),
                        textAlign = TextAlign.Start
                    )
                }
                Divider()
                AnimatedVisibility(visible = true) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = spacing.spaceSmall
                            )
                            .clickable {
                                openDialog.value = true
                            }
                    ) {

                        Text(text = state.timeoutDuration.toString() + stringResource(id = R.string.minutes))
                        Text(text = stringResource(R.string.tap_to_set_new_time))
                        Divider()
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = AlertDialogDefaults.TonalElevation,
//                color = Page
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    Text(
                        text = stringResource(R.string.remind_me_to_stop_scrolling_after),
                        style = MaterialTheme.typography.headlineMedium,
//                        color = Black
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    val npVal = remember {
                        mutableIntStateOf(state.timeoutDuration)
                    }
                    Row {
                        val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
                        AndroidView(
                            factory = { context ->
                                val np = NumberPicker(context)
                                np.maxValue = 60
                                np.minValue = 1
                                np.value = state.timeoutDuration
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    np.textColor = textColor
                                }
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
                            style = MaterialTheme.typography.headlineMedium,
//                            color = Black
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
                                style = MaterialTheme.typography.headlineSmall
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
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                }
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
