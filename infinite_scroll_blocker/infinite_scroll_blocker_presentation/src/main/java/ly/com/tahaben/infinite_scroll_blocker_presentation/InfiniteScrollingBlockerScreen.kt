package ly.com.tahaben.infinite_scroll_blocker_presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.OnLifecycleEvent
import ly.com.tahaben.core_ui.components.AccessibilityNotRunningContent
import ly.com.tahaben.core_ui.components.DropDownTextField
import ly.com.tahaben.core_ui.components.HowDialog
import ly.com.tahaben.core_ui.components.MyDialog
import ly.com.tahaben.core_ui.components.NumberPickerDialog
import ly.com.tahaben.core_ui.components.PermissionNotGrantedContent
import ly.com.tahaben.core_ui.components.getAnnotatedStringBulletList
import ly.com.tahaben.core_ui.mirror
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfiniteScrollingBlockerScreen(
    onNavigateUp: () -> Unit,
    onNavigateToExceptions: () -> Unit,
    viewModel: InfiniteScrollBlockerViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val openDialog = remember { mutableStateOf(false) }
    val openHowDialog = remember { mutableStateOf(false) }
    val openHowAccessibilityDialog = remember { mutableStateOf(false) }
    val onEvent = viewModel::onEvent

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
                    .selectableGroup()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.spaceMedium)
                        .selectable(
                            selected = state.isServiceEnabled,
                            onClick = { viewModel.setServiceStats(!state.isServiceEnabled) },
                            role = Role.Switch
                        ),
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
                HorizontalDivider()
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
                HorizontalDivider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.spaceMedium)
                        .clickable {
                            openDialog.value = true
                        }
                ) {

                    Text(text = state.timeoutDuration.toString() + stringResource(id = R.string.minutes))
                    Text(text = stringResource(R.string.tap_to_set_new_time))
                }
                HorizontalDivider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.spaceMedium)
                        .clickable {
                            onEvent(InfiniteScrollEvent.OnMsgDialogVisible(true))
                        }
                ) {

                    Text(
                        text = stringResource(id = R.string.msg_isb_timout),
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = stringResource(R.string.msg_isb_timout_sub))
                    Text(
                        text = stringResource(
                            id = R.string.selected_msg,
                            state.selectedMessage.asString(context)
                        ), maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                HorizontalDivider()
                val countDownEnabled by
                derivedStateOf { state.countDownSeconds > 0 }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.spaceMedium)
                        .selectable(
                            selected = countDownEnabled,
                            onClick = {
                                if (!countDownEnabled) {
                                    onEvent(InfiniteScrollEvent.OnCountDownDialogVisible(true))
                                } else {
                                    onEvent(InfiniteScrollEvent.SaveCountDownTime(-1))
                                }
                            },
                            role = Role.Switch
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.count_down_after_limit_isb),
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = stringResource(id = R.string.count_down_after_limit_sub))
                    }
                    Switch(
                        checked = countDownEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                onEvent(InfiniteScrollEvent.OnCountDownDialogVisible(true))
                            } else {
                                onEvent(InfiniteScrollEvent.SaveCountDownTime(-1))
                            }
                        }
                    )
                }
                HorizontalDivider()
                AnimatedVisibility(visible = countDownEnabled) {
                    Column(modifier = Modifier) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = spacing.spaceMedium)
                                .clickable {
                                    onEvent(InfiniteScrollEvent.OnCountDownDialogVisible(true))
                                }
                        ) {

                            Text(text = state.countDownSeconds.toString() + stringResource(id = R.string.seconds))
                            Text(text = stringResource(R.string.tap_to_set_new_time))
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        NumberPickerDialog(
            onDismissDialog = { openDialog.value = false },
            initialValue = state.timeoutDuration,
            minValue = 1,
            maxValue = 60,
            unit = stringResource(id = R.string.minutes),
            onValueChangedListener = { _, _ -> },
            onConfirmValue = {
                viewModel.setTimeoutDuration(it)
                openDialog.value = false
            }
        )
    }
    if (state.isCountDownDialogVisible) {
        NumberPickerDialog(
            onDismissDialog = { onEvent(InfiniteScrollEvent.OnCountDownDialogVisible(false)) },
            initialValue = state.countDownSeconds.absoluteValue,
            minValue = 1,
            maxValue = 30,
            unit = stringResource(id = R.string.seconds),
            onValueChangedListener = { _, _ -> },
            onConfirmValue = { onEvent(InfiniteScrollEvent.SaveCountDownTime(it)) }
        )
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
    if (state.isMsgDialogVisible) {
        val msgsScroll = rememberScrollState()
        MyDialog(onDismissRequest = { onEvent(InfiniteScrollEvent.OnMsgDialogVisible(false)) }) {
            Crossfade(state.isMsgDialogInEditMode, label = "message dialog") { isEditMode ->
                Column {
                    if (isEditMode) {
                        // Edit mode
                        Text(text = stringResource(id = R.string.msg_dialog_edit_mode))
                        Column(
                            modifier = Modifier
                                .height(200.dp)
                                .verticalScroll(msgsScroll),
                            verticalArrangement = Arrangement.spacedBy(spacing.spaceExtraSmall)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.weight(0.80f),
                                    value = state.msgTextFieldValue,
                                    label = { Text(text = stringResource(id = R.string.msg_isb_timout)) },
                                    onValueChange = {
                                        onEvent(
                                            InfiniteScrollEvent.OnMsgTextFieldValueChange(it)
                                        )
                                    })
                                IconButton(
                                    modifier = Modifier.weight(0.20f),
                                    onClick = { onEvent(InfiniteScrollEvent.OnAddMsg(state.msgTextFieldValue)) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = stringResource(id = R.string.add_icon)
                                    )
                                }
                            }
                            for (msg in state.msgSet) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        modifier = Modifier.weight(0.80f),
                                        text = msg
                                    )
                                    IconButton(
                                        modifier = Modifier.weight(0.20f),
                                        onClick = { onEvent(InfiniteScrollEvent.OnDeleteMsg(msg)) }) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = stringResource(
                                                id = R.string.delete_icon
                                            )
                                        )
                                    }
                                }
                                HorizontalDivider()
                            }
                        }

                        Text(
                            modifier = Modifier.padding(ButtonDefaults.TextButtonContentPadding).pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        onEvent(InfiniteScrollEvent.ResetMessages)
                                    }
                                )
                            },
                            text = stringResource(id = R.string.reset_messages),
                            color = MaterialTheme.colorScheme.primary
                        )

                        TextButton(onClick = { onEvent(InfiniteScrollEvent.OnSwitchMsgDialogMode) }) {
                            Text(
                                text = stringResource(id = R.string.back_to_select_msg)
                            )
                        }
                    } else {
                        // select mode
                        Text(text = stringResource(id = R.string.msg_dialog_text))
                        Row {
                            DropDownTextField(
                                menuModifier = Modifier,
                                readOnly = true,
                                menuExpanded = state.isMsgDropdownExpanded,
                                onExpandedChanged = {
                                    onEvent(
                                        InfiniteScrollEvent.OnMsgDropDownExpanded(
                                            it
                                        )
                                    )
                                },
                                text = state.selectedMessage.asString(context),
                                onTextChange = { }
                            ) {
                                for (msg in state.msgSet) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = msg
                                            )
                                        },
                                        onClick = { onEvent(InfiniteScrollEvent.OnMsgSelected(msg)) },
                                    )
                                    HorizontalDivider()
                                }
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(id = R.string.msg_random)
                                        )
                                    },
                                    onClick = { onEvent(InfiniteScrollEvent.OnMsgSelected("")) },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Shuffle,
                                            contentDescription = stringResource(id = R.string.msg_random)
                                        )
                                    })
                            }
                        }
                        TextButton(onClick = { onEvent(InfiniteScrollEvent.OnSwitchMsgDialogMode) }) {
                            Text(
                                text = stringResource(id = R.string.msg_dialog_edit_mode),

                            )
                        }
                        Row {
                            TextButton(onClick = {
                                onEvent(
                                    InfiniteScrollEvent.OnMsgDialogVisible(
                                        false
                                    )
                                )
                            }) {
                                Text(
                                    text = stringResource(id = R.string.cancel),

                                )
                            }
                            TextButton(onClick = {
                                onEvent(
                                    InfiniteScrollEvent.OnMsgDialogVisible(
                                        false
                                    )
                                )
                            }) {
                                Text(
                                    text = stringResource(id = R.string.save),

                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
