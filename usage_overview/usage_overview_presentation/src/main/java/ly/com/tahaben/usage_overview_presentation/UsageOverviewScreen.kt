package ly.com.tahaben.usage_overview_presentation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core_ui.*
import ly.com.tahaben.core_ui.components.HowDialog
import ly.com.tahaben.core_ui.components.PermissionNotGrantedContent
import ly.com.tahaben.usage_overview_presentation.components.*
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageOverviewScreen(
    onNavigateUp: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: UsageOverviewViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState,
    startDate: String? = null,
    endDate: String? = null
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    var openDateRangePicker by remember { mutableStateOf(false) }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.checkUsagePermissionState()
                viewModel.checkIfCachingEnabled()
                viewModel.setRange(startDate, endDate)
            }
            else -> Unit
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                UiEvent.NavigateUp -> TODO()
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message.asString(context))
                }
                else -> Unit
            }
        }
    }

    Column {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.usage)) },
            backgroundColor = White,
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        modifier = Modifier.mirror(),
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.onEvent(UsageOverviewEvent.OnShowDropDown) }) {
                    Icon(Icons.Default.MoreVert, stringResource(R.string.drop_down_menu))
                }
                DropdownMenu(
                    expanded = state.isDropDownMenuVisible,
                    onDismissRequest = { viewModel.onEvent(UsageOverviewEvent.OnDismissDropDown) }
                ) {
                    if (state.isCachingEnabled) {
                        DropdownMenuItem(onClick = {
                            viewModel.onEvent(UsageOverviewEvent.OnDismissDropDown)
                            viewModel.onEvent(UsageOverviewEvent.OnShowConfirmDeleteDialog)
                            Timber.d("show dialog")
                        }, enabled = !state.isModeRange && !state.isLoading) {
                            Text(text = stringResource(R.string.delete_cache_for_day))
                        }
                    }
                    DropdownMenuItem(onClick = {
                        viewModel.onEvent(UsageOverviewEvent.OnDismissDropDown)
                        onNavigateToSettings()
                    }) {
                        Text(text = stringResource(R.string.usage_settings))
                    }
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
                        isRangeMode = state.isModeRange,
                        date = state.date,
                        isToday = state.isDateToday,
                        isLoading = state.isLoading,
                        onPreviousDayClick = {
                            viewModel.onEvent(UsageOverviewEvent.OnPreviousDayClick)
                        },
                        onNextDayClick = {
                            viewModel.onEvent(UsageOverviewEvent.OnNextDayClick)
                        },
                        onDayClick = {
                            openDateRangePicker = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.spaceMedium),
                        disableRangeMode = { viewModel.onEvent(UsageOverviewEvent.OnDisableRangeMode) },
                        dateRangeStart = state.rangeStartDate,
                        dateRangeEnd = state.rangeEndDate
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
    val dateRangeState = rememberDateRangePickerState(
        yearRange = 2022..state.currentYear
    )


    AnimatedVisibility(
        openDateRangePicker,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Black.copy(alpha = 0.8f))
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center),
                shape = RoundedCornerShape(25.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colors.secondary)
                        .padding(vertical = spacing.spaceMedium)
                ) {
                    DateRangePicker(
                        modifier = Modifier
                            .height(500.dp),
                        state = dateRangeState,
                        colors = DatePickerDefaults.colors(

                            dayInSelectionRangeContainerColor = MaterialTheme.colors.primary,
                            selectedDayContainerColor = MaterialTheme.colors.primaryVariant,
                            selectedDayContentColor = MaterialTheme.colors.onPrimary,
                            todayDateBorderColor = MaterialTheme.colors.primary,
                        ),
                        dateValidator = { date ->
                            Timber.d("validator current date: $date")
                            viewModel.isDayInUpdatedDays(date)
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.usage_date_range_picker_title),
                                style = MaterialTheme.typography.h3
                            )
                        },
                        headline = {
                            Text(
                                text = stringResource(R.string.usage_date_range_picker_description),
                                style = MaterialTheme.typography.h5
                            )
                        },
                        showModeToggle = false
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.spaceSmall),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            modifier = Modifier.padding(spacing.spaceSmall),
                            onClick = {
                                openDateRangePicker = false
                                viewModel.onEvent(
                                    UsageOverviewEvent.OnRangeSelected(
                                        dateRangeState.selectedStartDateMillis,
                                        dateRangeState.selectedEndDateMillis
                                    )
                                )
                            }) {
                            Text(
                                text = stringResource(id = R.string.confirm),
                                style = MaterialTheme.typography.h5,
                                color = MaterialTheme.colors.primaryVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(spacing.spaceSmall))
                        TextButton(
                            modifier = Modifier.padding(spacing.spaceSmall),
                            onClick = { openDateRangePicker = false }) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                style = MaterialTheme.typography.h5,
                                color = MaterialTheme.colors.primaryVariant
                            )
                        }
                    }
                }
            }
        }
    }
    if (state.isDeleteDialogVisible) {
        Timber.d("show dialog")
        ConfirmDeleteDialog(
            onDismiss = { viewModel.onEvent(UsageOverviewEvent.OnDismissConfirmDeleteDialog) },
            date = parseDateText(date = state.date),
            onConfirm = { viewModel.onEvent(UsageOverviewEvent.OnDeleteCacheForDay) }
        )
    }
}