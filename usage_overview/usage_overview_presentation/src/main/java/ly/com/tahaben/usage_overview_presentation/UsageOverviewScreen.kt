package ly.com.tahaben.usage_overview_presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.OnLifecycleEvent
import ly.com.tahaben.core_ui.components.HowDialog
import ly.com.tahaben.core_ui.components.PermissionNotGrantedContent
import ly.com.tahaben.usage_overview_presentation.components.ConfirmDeleteDialog
import ly.com.tahaben.usage_overview_presentation.components.DaySelector
import ly.com.tahaben.usage_overview_presentation.components.TrackedAppItem
import ly.com.tahaben.usage_overview_presentation.components.UsageOverviewHeader
import ly.com.tahaben.usage_overview_presentation.components.parseDateText
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageOverviewScreen(
    onNavigateUp: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: UsageOverviewViewModel = hiltViewModel(),
    scaffoldState: SnackbarHostState,
    startDate: String? = null,
    endDate: String? = null
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val context = LocalContext.current
    val selectDateBottomSheetState =
        rememberModalBottomSheetState()

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.checkUsagePermissionState()
                viewModel.checkIfCachingEnabled()
                viewModel.iniFilters()
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
                    scaffoldState.showSnackbar(event.message.asString(context))
                }

                UiEvent.ShowBottomSheet -> {
                    // no longer needed in M3
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            },
            actions = {
                IconButton(
                    enabled = state.isUsagePermissionGranted,
                    onClick = { viewModel.onEvent(UsageOverviewEvent.OnShowDropDown) }) {
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
                        }, enabled = !state.isModeRange && !state.isLoading,
                            text = { Text(text = stringResource(R.string.delete_cache_for_day)) })
                    }
                    DropdownMenuItem(onClick = {
                        viewModel.onEvent(UsageOverviewEvent.OnDismissDropDown)
                        onNavigateToSettings()
                    },
                        text = { Text(text = stringResource(R.string.usage_settings)) })
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
                    AnimatedVisibility(visible = !state.isLoading && state.trackedApps.isNotEmpty()) {
                        Column(modifier = Modifier) {
                            Text(
                                modifier = Modifier
                                    .padding(start = spacing.spaceMedium),
                                text = stringResource(id = R.string.applications),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.75f
                                )
                            )
                            Spacer(modifier = Modifier.height(spacing.spaceSmall))
                        }
                    }
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
                                    style = MaterialTheme.typography.bodyLarge,
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

    if (state.isSelectDateBottomSheetVisible) {
        ModalBottomSheet(
            modifier = Modifier,
            sheetState = selectDateBottomSheetState,
            onDismissRequest = {
                viewModel.onEvent(UsageOverviewEvent.OnDismissDateBottomSheet)
            },
            content = {
                Column(
                    modifier = Modifier
                        //.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spacing.spaceHuge)
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
                    HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.spaceSmall))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spacing.spaceHuge)
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
        )
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DatePickerDialog(
    onEvent: (UsageOverviewEvent) -> Unit,
    isDateValid: (Long) -> Boolean
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return isDateValid(utcTimeMillis)
            }
        }
    )
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    DatePickerDialog(
//        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.tertiary),
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
            /*colors = DatePickerDefaults.colors(
                dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary,
                selectedDayContainerColor = MaterialTheme.colorScheme.secondary,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                todayDateBorderColor = MaterialTheme.colorScheme.primary,
            ),*/
            state = datePickerState
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
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return isDateValid(utcTimeMillis)
            }
        },
        yearRange = 2022..currentYear
    )
    val confirmEnabled = remember {
        derivedStateOf { dateRangeState.selectedStartDateMillis != null && dateRangeState.selectedEndDateMillis != null }
    }
    DatePickerDialog(
//        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.tertiary),
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
                dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary,
                selectedDayContainerColor = MaterialTheme.colorScheme.secondary,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                todayDateBorderColor = MaterialTheme.colorScheme.primary,
            ),
            showModeToggle = true
        )
    }
}