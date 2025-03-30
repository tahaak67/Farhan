package ly.com.tahaben.launcher_presentation.wait

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.OnLifecycleEvent
import ly.com.tahaben.core_ui.components.AccessibilityNotRunningContent
import ly.com.tahaben.core_ui.components.CommonAppBar
import ly.com.tahaben.core_ui.components.DropDownTextField
import ly.com.tahaben.core_ui.components.HowDialog
import ly.com.tahaben.core_ui.components.MyDialog
import ly.com.tahaben.core_ui.components.SwitchRow
import ly.com.tahaben.core_ui.components.getAnnotatedStringBulletList
import ly.com.tahaben.core_ui.navigation.openAccessibilitySettings
import ly.com.tahaben.core_ui.theme.FarhanTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelayedLaunchScreen(
    onNavigateUp: () -> Unit,
    onNavigateToWhitelist: () -> Unit,
    onEvent: (DelayedLaunchEvent) -> Unit,
    state: DelayedLaunchState
) {
    val spacing = LocalSpacing.current
    var openHowAccessibilityDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var delayDurationDialogVisible by remember { mutableStateOf(false) }
    var delayMsgDialogVisible by remember { mutableStateOf(false) }
    var delayMsgDialogEditMode by remember { mutableStateOf(false) }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                onEvent(DelayedLaunchEvent.ScreenShown)
            }

            else -> Unit
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonAppBar(
                title = stringResource(R.string.delayed_launch),
                onNavigateUp = {
                    onNavigateUp()
                }
            )
        }
    ) { paddingValues ->
        Column(Modifier
            .padding(paddingValues)
            .fillMaxSize()) {

            if (state.isAccessibilityPermissionGranted){
                SwitchRow(
                    Modifier
                        .fillMaxWidth(),
                    stringResource(R.string.delayed_launch),
                    selected = state.isMindfulLaunchEnabled,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    onEvent(DelayedLaunchEvent.OnDelayedLaunchEnabled(it))
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.spaceMedium))
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onNavigateToWhitelist()
                        },
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.white_lited_apps),
                            textAlign = TextAlign.Start
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.spaceMedium))
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            delayDurationDialogVisible = true
                        },
                    headlineContent = {
                        Row(modifier = Modifier) {
                            Text(
                                text = stringResource(R.string.delay_in_seconds),
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ": ${state.delayDurationSeconds}",
                                textAlign = TextAlign.Start
                            )
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.spaceMedium))
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            delayMsgDialogVisible = true
                        },
                    headlineContent = {
                        Row(modifier = Modifier) {
                            Text(
                                text = stringResource(R.string.delay_message),
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ": ${state.selectedDelayedLaunchMessage.ifEmpty { stringResource(R.string.msg_random) }}",
                                textAlign = TextAlign.Start,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.delay_message_sub),
                            textAlign = TextAlign.Start
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
                )

            } else {
// accessibility not running
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    val messages = listOf(
                        stringResource(R.string.reason_know_what_app_launched)
                    )
                    val permissionReasons =
                        getAnnotatedStringBulletList(messages)

                    AccessibilityNotRunningContent(
                        modifier = Modifier,
                        message = stringResource(R.string.accessibility_permission_not_granted),
                        subMessage = stringResource(R.string.accessibility_permission_needed_message),
                        permissionReasons = permissionReasons,
                        onGrantClick = { openAccessibilitySettings(context) },
                        onBack = onNavigateUp,
                        onHowClick = { openHowAccessibilityDialog = true }
                    )
                }

            }
        }
        if (delayDurationDialogVisible){
            MyDialog(onDismissRequest = {delayDurationDialogVisible = false}) {
                var text by remember { mutableStateOf(state.delayDurationSeconds.toString()) }
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        if (it.isBlank()) text = ""
                        if (it.toIntOrNull() == null) return@OutlinedTextField
                        text = it },
                    label = {
                        Text(
                            stringResource(R.string.delay_in_seconds)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(spacing.spaceMedium)) {
                    Button(
                        onClick = {
                            onEvent(DelayedLaunchEvent.OnSetDelayDuration(text.toInt()))
                            delayDurationDialogVisible = false
                                  },
                        enabled = text.isNotBlank()
                        ) {
                        Text(stringResource(R.string.save))
                    }
                    Button(onClick = {delayDurationDialogVisible = false}) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
        if (delayMsgDialogVisible){
            MyDialog(onDismissRequest = {delayMsgDialogVisible = false}) {
                var selectedMessage by remember { mutableStateOf(state.selectedDelayedLaunchMessage) }
                var msgTextFieldValue by remember { mutableStateOf("") }
                var isMsgDropdownExpanded by remember { mutableStateOf(false) }
                val msgsScroll = rememberScrollState()
                if (delayMsgDialogEditMode) {
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
                                value = msgTextFieldValue,
                                label = { Text(text = stringResource(id = R.string.msg_isb_timout)) },
                                onValueChange = {
                                    msgTextFieldValue = it
                                })
                            IconButton(
                                modifier = Modifier.weight(0.20f),
                                onClick = {
                                    onEvent(DelayedLaunchEvent.AddMsgToDelayMessages(msgTextFieldValue))
                                    msgTextFieldValue = ""
                                }) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = stringResource(id = R.string.add_icon)
                                )
                            }
                        }
                        for (msg in state.delayedLaunchMessages) {
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
                                    onClick = {
                                        onEvent(DelayedLaunchEvent.DeleteDelayMsg(msg))
                                    }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = stringResource(id = R.string.delete_icon)
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
                                    onEvent(DelayedLaunchEvent.ResetDelayMessages)
                                }
                            )
                        },
                        text = stringResource(id = R.string.reset_messages),
                        color = MaterialTheme.colorScheme.primary
                    )

                    TextButton(onClick = {
                        delayMsgDialogEditMode = !delayMsgDialogEditMode
                    }) {
                        Text(text = stringResource(id = R.string.back_to_select_msg))
                    }
                } else {

                    DropDownTextField(
                        menuModifier = Modifier,
                        readOnly = true,
                        menuExpanded = isMsgDropdownExpanded,
                        onExpandedChanged = {
                            isMsgDropdownExpanded = it
                        },
                        text = selectedMessage.ifEmpty { stringResource(R.string.msg_random) },
                        onTextChange = { }
                    ) {
                        for (msg in state.delayedLaunchMessages) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = msg)
                                },
                                onClick = {
                                    isMsgDropdownExpanded = false
                                    selectedMessage = msg
                                },
                            )
                            HorizontalDivider()
                        }
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = R.string.msg_random))
                            },
                            onClick = {
                                selectedMessage = ""
                                isMsgDropdownExpanded = false
                                      },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Shuffle,
                                    contentDescription = stringResource(id = R.string.msg_random)
                                )
                            })
                    }
                    TextButton(onClick = { delayMsgDialogEditMode = !delayMsgDialogEditMode }) {
                        Text(text = stringResource(id = R.string.msg_dialog_edit_mode))
                    }
                    Row {
                        TextButton(onClick = {
                                delayMsgDialogVisible = false
                        }) {
                            Text(text = stringResource(id = R.string.cancel),)
                        }
                        TextButton(onClick = {
                            onEvent(DelayedLaunchEvent.SetDelayMsg(selectedMessage))
                            delayMsgDialogVisible = false
                        }) {
                            Text(text = stringResource(id = R.string.save),)
                        }
                    }
                }
            }
        }
    }
    if (openHowAccessibilityDialog) {
        HowDialog(
            gifId = R.drawable.accessibility_permission_howto,
            gifDescription = stringResource(R.string.how_to_enable_permission),
            onDismiss = { openHowAccessibilityDialog = false }
        )
    }
}

@Preview()
@Preview("ar", locale = "ar")
@Composable
private fun DelayedLaunchScreenPreview() {

    FarhanTheme(false, ThemeColors.Classic) {
        DelayedLaunchScreen(
            onNavigateUp = {},
            onNavigateToWhitelist = {},
            onEvent = {},
            state = DelayedLaunchState()
        )
    }
}