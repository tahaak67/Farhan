package ly.com.tahaben.notification_filter_presentation.settings

import android.app.TimePickerDialog
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.mirror
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationFilterSettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToExceptions: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val state = viewModel.state
    val isServiceChecked = remember { mutableStateOf(state.isServiceEnabled) }
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    Column(
        Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.notifications_filter_settings))
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
        Column(
            modifier = Modifier
                .padding(horizontal = spacing.spaceMedium)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.spaceMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Text(text = stringResource(R.string.notifications_filter))
                Switch(
                    checked = isServiceChecked.value,
                    onCheckedChange = { checked ->
                        viewModel.setServiceStats(checked)
                        isServiceChecked.value = checked
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.spaceMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(text = stringResource(R.string.notify_me))
                    Text(
                        text = stringResource(R.string.notify_me_description),
                        textAlign = TextAlign.Start
                    )
                }
                Switch(
                    checked = state.isNotifyMeEnabled,
                    onCheckedChange = { checked ->
                        if (!checked) {
                            viewModel.setNotifyMeTime(-1, -1)
                        } else {
                            val tpd = TimePickerDialog(
                                context, { _, mHour: Int, mMinute: Int ->
                                    viewModel.setNotifyMeTime(mHour, mMinute)
                                }, mHour, mMinute, false
                            )
                            tpd.updateTime(state.notifyMeHour, state.notifyMeMinute)
                            tpd.show()
                            tpd.setOnCancelListener {
                                viewModel.onEvent(NotificationSettingsEvent.CancelNotifyMe)
                            }
                        }
                    }
                )
            }
            //Todo: wait for time picker api to be updated
            /*if (showTimePicker){
                Dialog(onDismissRequest = { showTimePicker = false }) {
                    //TODO()
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        tonalElevation = AlertDialogDefaults.TonalElevation
                    ) {
                        Column(
                            modifier = Modifier.padding(spacing.spaceLarge)
                        ) {
                            TimePicker(
                                state = timePickerState,
                                colors = TimePickerDefaults.colors(
                                    clockDialColor = MaterialTheme.colorScheme.background,
                                    clockDialSelectedContentColor = MaterialTheme.colorScheme.onBackground,
                                    clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f),
                                    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
                                    selectorColor = DarkerYellowDark,
                                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onBackground,
                                    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.background,
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                                    periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
                                )
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showTimePicker = false }) {
                                    Text(
                                        text = stringResource(id = R.string.cancel),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                Spacer(modifier = Modifier.width(spacing.spaceSmall))
                                TextButton(onClick = {
                                    viewModel.setNotifyMeTime(timePickerState.hour,timePickerState.minute)
                                    showTimePicker = false
                                }) {
                                    Text(
                                        text = stringResource(id = R.string.ok),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }

                            }
                        }
                    }
                }
            }*/
            AnimatedVisibility(visible = state.isNotifyMeEnabled) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.spaceSmall)
                        .clickable {
                            TimePickerDialog(
                                context, { _, mHour: Int, mMinute: Int ->
                                    viewModel.setNotifyMeTime(mHour, mMinute)
                                }, mHour, mMinute, false
                            ).show()
                        },
                ) {
                    if (state.notifyMeHour != -1) {
                        Text(text = "${state.notifyMeHour}:${state.notifyMeMinute}")
                    }
                    Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))
                    Text(text = stringResource(R.string.tap_to_set_new_time))
                }
            }
            Divider()
        }

    }
}

