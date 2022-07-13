package ly.com.tahaben.usage_overview_presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.*
import ly.com.tahaben.usage_overview_presentation.components.DaySelector
import ly.com.tahaben.usage_overview_presentation.components.TrackedAppItem
import ly.com.tahaben.usage_overview_presentation.components.UsageOverviewHeader


@Composable
fun UsageOverviewScreen(
    onNavigateUp: () -> Unit,
    viewModel: UsageOverviewViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.checkUsagePermissionState()
            }
            else -> Unit
        }
    }

    Column {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.usage)) },
            backgroundColor = White,
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        )
        if (state.isUsagePermissionGranted) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = spacing.spaceMedium)
            ) {
                item {
                    UsageOverviewHeader(state = state)
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    DaySelector(
                        date = state.date,
                        isToday = state.isDateToday,
                        isLoading = state.isLoading,
                        onPreviousDayClick = {
                            viewModel.onEvent(UsageOverviewEvent.OnPreviousDayClick)
                        },
                        onNextDayClick = {
                            viewModel.onEvent(UsageOverviewEvent.OnNextDayClick)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.spaceMedium)
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            state.isLoading -> {
                                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                                CircularProgressIndicator()
                            }
                            state.trackedApps.isEmpty() -> {
                                Text(
                                    text = stringResource(id = R.string.no_results),
                                    style = MaterialTheme.typography.body1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                items(state.trackedApps) { app ->
                    TrackedAppItem(
                        trackedApp = app,
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                }
            }
        } else {
            PermissionNotGrantedContent(
                modifier = Modifier.fillMaxSize(),
                message = stringResource(R.string.usage_permission_msg),
                subMessage = stringResource(R.string.usage_permission_sub_msg),
                onGrantClick = viewModel::askForUsagePermission,
                onHowClick = { openDialog.value = true }
            )
        }
    }
    if (openDialog.value) {
        HowDialog(
            gifId = R.drawable.usage_permission_howto,
            gifDescription = stringResource(R.string.how_to_enable_permission),
            onDismiss = { openDialog.value = false }
        )
    }
}