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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val state = viewModel.state
    val isServiceChecked = remember { mutableStateOf(state.isServiceEnabled) }
    val timeOutDuration = remember { mutableStateOf(state.timeoutDuration) }
    val openDialog = remember { mutableStateOf(false) }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.spaceMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Appear on top permission is not granted :(",
                    style = MaterialTheme.typography.h1,
                    color = Black
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Text(
                    text = "Farhan needs Appear on top access to help you break Infinite scrolling",
                    style = MaterialTheme.typography.h3,
                    color = Black
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Button(onClick = { viewModel.askForAppearOnTopPermission() }) {
                    Text(
                        text = "Grant access",
                        style = MaterialTheme.typography.button,
                        color = Black
                    )
                }
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
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


    // below line is to check if the
    // dialog box is open or not.
    if (openDialog.value) {
        // below line is use to
        // display a alert dialog.
        Dialog(
            // on dialog dismiss we are setting
            // our dialog value to false.
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
                    text = "Remind me to stop after",
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
                        // adding on click listener for this button
                        onClick = {
                            openDialog.value = false
                        }
                    ) {
                        // adding text to our button.
                        Text(
                            "Dismiss",
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
                        // in this line we are adding
                        // text for our confirm button.
                        Text(
                            "Confirm",
                            style = MaterialTheme.typography.h5
                        )
                    }
                }
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
            }

        }
    }
}


