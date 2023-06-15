package ly.com.tahaben.usage_overview_presentation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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


@OptIn(ExperimentalMaterialApi::class)
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
    val selectDateBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

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

                UiEvent.ShowBottomSheet -> {
                    selectDateBottomSheetState.show()
                }

                UiEvent.DismissBottomSheet -> {
                    selectDateBottomSheetState.hide()
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
                            viewModel.onEvent(UsageOverviewEvent.OnShowDateBottomSheet)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.spaceMedium),
                        dateRangeStart = state.rangeStartDate,
                        dateRangeEnd = state.rangeEndDate
                    )
                    UsageOverviewHeader(state = state)
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
                    Spacer(modifier = Modifier.height(spacing.spaceSmall))
                }
            }
        } else {
            PermissionNotGrantedContent(
                modifier = Modifier.fillMaxSize(),
                message = stringResource(R.string.usage_permission_msg),
                subMessage = stringResource(R.string.usage_permission_sub_msg),
                onGrantClick = viewModel::askForUsagePermission,
                onHowClick = { viewModel.onEvent(UsageOverviewEvent.OnShowHowDialog) }
            )
        }
    }
    if (state.isHowDialogVisible) {
        HowDialog(
            gifId = R.drawable.usage_permission_howto,
            gifDescription = stringResource(R.string.how_to_enable_permission),
            onDismiss = { viewModel.onEvent(UsageOverviewEvent.OnDismissHowDialog) }
        )
    }

    if (state.isDatePickerDialogVisible) {
        DatePickerDialog(viewModel::onEvent, viewModel::isDayInUpdatedDays)
    }

    if (state.isRangePickerDialogVisible) {
        DateRangePickerDialog(state.currentYear, viewModel::onEvent, viewModel::isDayInUpdatedDays)
    }

    ModalBottomSheetLayout(
        modifier = Modifier,
        sheetState = selectDateBottomSheetState,
        sheetBackgroundColor = Color.Transparent,
        sheetContent = {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(MaterialTheme.colors.surface),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.spaceExtraLarge)
                        .clickable {
                            viewModel.onEvent(UsageOverviewEvent.OnSelectDateClick)
                        }
                        .padding(horizontal = spacing.spaceSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.date_calendar),
                        contentDescription = stringResource(R.string.select_a_day)
                    )
                    Spacer(modifier = Modifier.width(spacing.spaceMedium))
                    Text(text = stringResource(R.string.select_a_day))
                }
                Divider(modifier = Modifier.padding(horizontal = spacing.spaceSmall))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.spaceExtraLarge)
                        .clickable {
                            viewModel.onEvent(UsageOverviewEvent.OnSelectRangeClick)
                        }
                        .padding(horizontal = spacing.spaceSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.range_calendar),
                        contentDescription = stringResource(R.string.select_a_range)
                    )
                    Spacer(modifier = Modifier.width(spacing.spaceMedium))
                    Text(text = stringResource(R.string.select_a_range))
                }
            }
        },
        content = {}
    )
    if (state.isDeleteDialogVisible) {
        Timber.d("show dialog")
        ConfirmDeleteDialog(
            onDismiss = { viewModel.onEvent(UsageOverviewEvent.OnDismissConfirmDeleteDialog) },
            date = parseDateText(date = state.date),
            onConfirm = { viewModel.onEvent(UsageOverviewEvent.OnDeleteCacheForDay) }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DatePickerDialog(
    onEvent: (UsageOverviewEvent) -> Unit,
    isDateValid: (Long) -> Boolean
) {
    val datePickerState = rememberDatePickerState()
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    DatePickerDialog(
        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colors.secondary),
        onDismissRequest = { onEvent(UsageOverviewEvent.OnDismissDatePickerDialog) },
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis ?: return@TextButton
                    onEvent(UsageOverviewEvent.OnDateSelected(selectedDateMillis))
                },
                enabled = confirmEnabled.value
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(UsageOverviewEvent.OnDismissDatePickerDialog) }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    ) {
        DatePicker(
            colors = DatePickerDefaults.colors(
                dayInSelectionRangeContainerColor = MaterialTheme.colors.primary,
                selectedDayContainerColor = MaterialTheme.colors.primaryVariant,
                selectedDayContentColor = MaterialTheme.colors.onPrimary,
                todayDateBorderColor = MaterialTheme.colors.primary,
            ),
            state = datePickerState,
            dateValidator = { dateInMilli ->
                isDateValid(dateInMilli)
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DateRangePickerDialog(
    currentYear: Int,
    onEvent: (UsageOverviewEvent) -> Unit,
    isDateValid: (Long) -> Boolean
) {
    val spacing = LocalSpacing.current
    val dateRangeState = rememberDateRangePickerState(
        yearRange = 2022..currentYear
    )
    val confirmEnabled = remember {
        derivedStateOf { dateRangeState.selectedStartDateMillis != null && dateRangeState.selectedEndDateMillis != null }
    }
    DatePickerDialog(
        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colors.secondary),
        onDismissRequest = { onEvent(UsageOverviewEvent.OnDismissRangePickerDialog) },
        confirmButton = {
            TextButton(
                onClick = {
                    val startDateMillis = dateRangeState.selectedStartDateMillis
                    val endDateMillis = dateRangeState.selectedEndDateMillis
                    onEvent(
                        UsageOverviewEvent.OnRangeSelected(
                            startDateMillis,
                            endDateMillis
                        )
                    )
                },
                enabled = confirmEnabled.value
            ) {
                Text(
                    text = stringResource(id = R.string.confirm)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(UsageOverviewEvent.OnDismissRangePickerDialog) }) {
                Text(
                    text = stringResource(id = R.string.cancel)
                )
            }
        }
    ) {
        DateRangePicker(
            modifier = Modifier
                .heightIn(min = 250.dp, max = 500.dp)
                .padding(spacing.spaceMedium),
            state = dateRangeState,
            colors = DatePickerDefaults.colors(
                dayInSelectionRangeContainerColor = MaterialTheme.colors.primary,
                selectedDayContainerColor = MaterialTheme.colors.primaryVariant,
                selectedDayContentColor = MaterialTheme.colors.onPrimary,
                todayDateBorderColor = MaterialTheme.colors.primary,
            ),
            showModeToggle = true,
            dateValidator = { dateInMillis ->
                isDateValid(dateInMillis)
            }
        )
    }
}