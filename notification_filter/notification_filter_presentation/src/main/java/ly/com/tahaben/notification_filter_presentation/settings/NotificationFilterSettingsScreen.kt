package ly.com.tahaben.notification_filter_presentation.settings

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.White
import ly.com.tahaben.core_ui.mirror
import java.util.*

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
    val isNotifyMeChecked = remember { mutableStateOf(state.isNotifyMeEnabled) }
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    Column(
        Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.notifications_filter_settings))

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium),
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium)
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium),
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
                checked = isNotifyMeChecked.value,
                onCheckedChange = { checked ->
                    isNotifyMeChecked.value = checked
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
                    }
                }
            )
        }
        AnimatedVisibility(visible = isNotifyMeChecked.value) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmall)
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


    }
}

