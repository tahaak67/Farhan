package ly.com.tahaben.usage_overview_presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.model.UsageDurationDataItem
import ly.com.tahaben.usage_overview_domain.preferences.Preferences
import ly.com.tahaben.usage_overview_domain.use_case.UsageOverviewUseCases
import ly.com.tahaben.usage_overview_domain.use_case.UsageSettingsUseCases
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class UsageOverviewViewModel @Inject constructor(
    private val usageOverviewUseCases: UsageOverviewUseCases,
    private val usageSettingsUseCases: UsageSettingsUseCases,
    private val preferences: Preferences
) : ViewModel() {


    var state by mutableStateOf(UsageOverviewState())
        private set

    private var filterFarhan = false
    private var filterLaunchers = false

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var getUsageDataJob: Job? = null

    init {
        getFullyUpdatedDays()
    }

    fun iniFilters() {
        filterLaunchers = preferences.isIgnoreLauncher()
        filterFarhan = preferences.isIgnoreFarhan()
    }

    private fun getFullyUpdatedDays() {
        viewModelScope.launch {
            val updatedDaysList = mutableListOf<LocalDate>()
            usageOverviewUseCases.getUpdatedDays().forEach { date ->
                if (usageOverviewUseCases.isDayDataFullyUpdated(date)) {
                    updatedDaysList.add(date)
                }
            }
            state = state.copy(
                fullyUpdatedDays = updatedDaysList
            )
            Timber.d("fully updated days ${state.fullyUpdatedDays}")
        }
    }


    fun checkUsagePermissionState() {
        state = state.copy(
            isUsagePermissionGranted = usageOverviewUseCases.isUsagePermissionGranted()
        )
        if (state.isUsagePermissionGranted && state.trackedApps.isEmpty()) {
            refreshUsageData()
        }
    }

    fun setRange(startDate: String?, endDate: String?) {
        Timber.d("viewmodel start end date: $startDate : $endDate")
        if (startDate != null && endDate == null){
            state = state.copy(
                date = LocalDate.parse(startDate),
                isModeRange = false
            )
            refreshUsageData()
        }
        if (startDate != null && endDate != null) {
            state = state.copy(
                rangeStartDate = LocalDate.parse(startDate),
                rangeEndDate = LocalDate.parse(endDate),
                isModeRange = true
            )
            refreshUsageDataForRange()
        }
    }

    fun askForUsagePermission() {
        refreshUsageData()
    }

    fun checkIfCachingEnabled() {
        state = state.copy(
            isCachingEnabled = usageSettingsUseCases.isCachingEnabled()
        )
    }

    private fun refreshUsageData() {
        getUsageDataJob?.cancel()
        getUsageDataJob = viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                totalUsageDuration = 0,
                totalUsageMilli = 0,
                totalSocialUsageMilli = 0,
                totalProductivityUsageMilli = 0,
                totalGameUsageMilli = 0,
                totalUsageMinutes = 0,
                trackedApps = emptyList(),
                isDateToday = usageOverviewUseCases.isDateToDay(state.date)
            )
            if (state.isCachingEnabled) {
                if (!usageOverviewUseCases.isDayDataFullyUpdated(state.date)) {
                    Timber.d("cache not updated ${state.date}")
                    usageOverviewUseCases.cacheUsageDataForDate(state.date)
                }
            }
            val usageDataList =
                if (state.isCachingEnabled) {
                    Timber.d("getting data from db")
                    usageOverviewUseCases.getUsageEventsFromDb(state.date)
                } else {
                    Timber.d("getting data from api")
                    usageOverviewUseCases.getUsageDataForDate(state.date)
                }
            Timber.d("is caching enabled: ${state.isCachingEnabled}")
            Timber.d("usageDataList: $usageDataList")
            Timber.d("usageDataList size: ${usageDataList.size}")

            val filteredList = if (state.isDateToday) {
                usageOverviewUseCases.filterUsageEvents(usageDataList).dropLast(1).filterNot {
                    (filterLaunchers && it.appCategory == UsageDataItem.Category.LAUNCHER) ||
                            (filterFarhan && it.packageName == "ly.com.tahaben.farhan")
                }
            } else {
                usageOverviewUseCases.filterUsageEvents(usageDataList).filterNot {
                    (filterLaunchers && it.appCategory == UsageDataItem.Category.LAUNCHER) ||
                            (filterFarhan && it.packageName == "ly.com.tahaben.farhan")
                }
            }
            Timber.d("filtered data size: ${filteredList.size}")
            Timber.d("usage list")

            val filteredListWithDuration = usageOverviewUseCases.calculateUsageDuration(
                filteredList,
                usageOverviewUseCases.getDurationFromMilliseconds,
                usageOverviewUseCases.filterDuration
            ).sortedByDescending { it.usageDurationInMilliseconds }
            Timber.d("Filtered list $filteredListWithDuration")
            val totalTimeInMilliSeconds =
                filteredListWithDuration.sumOf { it.usageDurationInMilliseconds }
            val totalSocialUsageMilli = filteredListWithDuration
                .filter { it.appCategory == UsageDataItem.Category.SOCIAL }
                .sumOf { it.usageDurationInMilliseconds }
            val totalProductivityUsageMilli = filteredListWithDuration
                .filter { it.appCategory == UsageDataItem.Category.PRODUCTIVITY }
                .sumOf { it.usageDurationInMilliseconds }
            val totalGameUsageMilli = filteredListWithDuration
                .filter { it.appCategory == UsageDataItem.Category.GAME }
                .sumOf { it.usageDurationInMilliseconds }
            totalTimeInMilliSeconds.milliseconds.toComponents { hours, minutes, _, _ ->
                state = state.copy(
                    totalSocialUsageMilli = totalSocialUsageMilli,
                    totalProductivityUsageMilli = totalProductivityUsageMilli,
                    totalGameUsageMilli = totalGameUsageMilli,
                    totalUsageMilli = totalTimeInMilliSeconds,
                    totalUsageDuration = hours.toInt(),
                    totalUsageMinutes = minutes,
                    trackedApps = filteredListWithDuration,
                    isLoading = false
                )
                Timber.d("posted data size is ${filteredListWithDuration.size}")
            }
        }
        getFullyUpdatedDays()
    }

    fun onEvent(event: UsageOverviewEvent) {
        viewModelScope.launch {
            when (event) {
                is UsageOverviewEvent.OnNextDayClick -> {
                    state = state.copy(
                        date = state.date.plusDays(1)
                    )
                    refreshUsageData()
                }
                is UsageOverviewEvent.OnPreviousDayClick -> {
                    state = state.copy(
                        date = state.date.minusDays(1)
                    )
                    refreshUsageData()
                }
                is UsageOverviewEvent.OnRangeSelected -> {
                    if (event.startDateMillis == null || event.endDateMillis == null) {
                        _uiEvent.send(UiEvent.ShowSnackbar(UiText.StringResource(R.string.range_null_error)))
                    } else {
                        state = state.copy(
                            rangeStartDate = timestampToLocalDate(event.startDateMillis),
                            rangeEndDate = timestampToLocalDate(event.endDateMillis),
                            isModeRange = true,
                            isRangePickerDialogVisible = false
                        )
                        refreshUsageDataForRange()
                    }
                }
                is UsageOverviewEvent.OnDisableRangeMode -> {
                    state = state.copy(
                        rangeStartDate = null,
                        rangeEndDate = null,
                        isModeRange = false
                    )
                    refreshUsageData()
                }
                UsageOverviewEvent.OnDismissDropDown -> {
                    state = state.copy(isDropDownMenuVisible = false)
                }
                UsageOverviewEvent.OnShowDropDown -> {
                    state = state.copy(isDropDownMenuVisible = true)
                }
                UsageOverviewEvent.OnShowConfirmDeleteDialog -> {
                    Timber.d("show dialog")
                    state = state.copy(isDeleteDialogVisible = true)
                }

                UsageOverviewEvent.OnDismissConfirmDeleteDialog -> {
                    state = state.copy(isDeleteDialogVisible = false)
                }

                UsageOverviewEvent.OnDeleteCacheForDay -> {
                    onEvent(UsageOverviewEvent.OnDismissConfirmDeleteDialog)
                    usageOverviewUseCases.deleteCacheForDay(state.date)
                    refreshUsageData()
                }

                UsageOverviewEvent.OnSelectDateClick -> {
                    _uiEvent.send(UiEvent.DismissBottomSheet)
                    state = state.copy(
                        isDatePickerDialogVisible = true,
                        isSelectDateBottomSheetVisible = false
                    )
                }

                UsageOverviewEvent.OnSelectRangeClick -> {
                    _uiEvent.send(UiEvent.DismissBottomSheet)
                    state = state.copy(
                        isRangePickerDialogVisible = true,
                        isSelectDateBottomSheetVisible = false
                    )
                }

                UsageOverviewEvent.OnShowDateBottomSheet -> {
                    _uiEvent.send(UiEvent.ShowBottomSheet)
                    state = state.copy(
                        isSelectDateBottomSheetVisible = true
                    )
                }

                UsageOverviewEvent.OnDismissDateBottomSheet -> {
//                    _uiEvent.send(UiEvent.DismissBottomSheet)
                    state = state.copy(
                        isSelectDateBottomSheetVisible = false
                    )
                }

                UsageOverviewEvent.OnShowDatePickerDialog -> {
                    state = state.copy(
                        isDatePickerDialogVisible = true
                    )
                }

                UsageOverviewEvent.OnDismissDatePickerDialog -> {
                    state = state.copy(
                        isDatePickerDialogVisible = false
                    )
                }

                UsageOverviewEvent.OnShowRangePickerDialog -> {
                    state = state.copy(
                        isRangePickerDialogVisible = true
                    )
                }

                UsageOverviewEvent.OnDismissRangePickerDialog -> {
                    state = state.copy(
                        isRangePickerDialogVisible = false
                    )
                }

                is UsageOverviewEvent.OnDateSelected -> {
                    val selectedDate = timestampToLocalDate(event.selectedDateMillis)
                    state = state.copy(
                        date = selectedDate,
                        isDatePickerDialogVisible = false,
                        isModeRange = false,
                        rangeStartDate = null,
                        rangeEndDate = null
                    )
                    refreshUsageData()
                }

                UsageOverviewEvent.OnShowHowDialog -> {
                    state = state.copy(
                        isHowDialogVisible = true
                    )
                }

                UsageOverviewEvent.OnDismissHowDialog -> {
                    state = state.copy(
                        isHowDialogVisible = false
                    )
                }
            }
        }
    }

    private fun timestampToLocalDate(timestamp: Long): LocalDate {
        val instant = Instant.ofEpochMilli(timestamp)
        val zoneId = ZoneId.systemDefault()
        val zonedDateTime = instant.atZone(zoneId)
        return zonedDateTime.toLocalDate()
    }

    private fun refreshUsageDataForRange() {
        getUsageDataJob?.cancel()
        getUsageDataJob = viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                totalUsageDuration = 0,
                totalUsageMilli = 1,
                totalSocialUsageMilli = 0,
                totalProductivityUsageMilli = 0,
                totalGameUsageMilli = 0,
                totalUsageMinutes = 0,
                trackedApps = emptyList(),
                isDateToday = usageOverviewUseCases.isDateToDay(state.date)
            )

            // checking cache
            var startDate = state.rangeStartDate
            val endDate = state.rangeEndDate
            if (startDate != null && endDate != null) {
                while (startDate!! <= endDate) {
                    if (!usageOverviewUseCases.isDayDataFullyUpdated(startDate)) {
                        usageOverviewUseCases.cacheUsageDataForDate(startDate)
                    }
                    startDate = startDate.plusDays(1)
                }
            }
            // calculation
            var date = state.rangeStartDate
            if (date != null) {
                val filteredListForRange = mutableListOf<UsageDurationDataItem>()
                while (date!! <= state.rangeEndDate) {
                    val usageDataList = usageOverviewUseCases.getUsageEventsFromDb(date)

                    val filteredList = if (usageOverviewUseCases.isDateToDay(date)){
                        usageOverviewUseCases.filterUsageEvents(usageDataList).dropLast(1).filterNot {
                            (filterLaunchers && it.appCategory == UsageDataItem.Category.LAUNCHER) ||
                                    (filterFarhan && it.packageName == "ly.com.tahaben.farhan")
                        }
                    } else {
                        usageOverviewUseCases.filterUsageEvents(usageDataList).filterNot {
                            (filterLaunchers && it.appCategory == UsageDataItem.Category.LAUNCHER) ||
                                    (filterFarhan && it.packageName == "ly.com.tahaben.farhan")
                        }
                    }
                    val filteredListWithDuration =
                        usageOverviewUseCases.calculateUsageDuration(
                            if (state.isDateToday) filteredList else filteredList,
                            usageOverviewUseCases.getDurationFromMilliseconds,
                            usageOverviewUseCases.filterDuration
                        ).sortedByDescending { it.usageDurationInMilliseconds }
                    filteredListForRange.addAll(filteredListWithDuration)
                    val totalTimeInMilliSeconds =
                        filteredListWithDuration.sumOf { it.usageDurationInMilliseconds }
                    val totalSocialUsageMilli = filteredListWithDuration
                        .filter { it.appCategory == UsageDataItem.Category.SOCIAL }
                        .sumOf { it.usageDurationInMilliseconds }
                    val totalProductivityUsageMilli = filteredListWithDuration
                        .filter { it.appCategory == UsageDataItem.Category.PRODUCTIVITY }
                        .sumOf { it.usageDurationInMilliseconds }
                    val totalGameUsageMilli = filteredListWithDuration
                        .filter { it.appCategory == UsageDataItem.Category.GAME }
                        .sumOf { it.usageDurationInMilliseconds }

                    state = state.copy(
                        totalSocialUsageMilli = totalSocialUsageMilli + state.totalSocialUsageMilli,
                        totalProductivityUsageMilli = totalProductivityUsageMilli + state.totalProductivityUsageMilli,
                        totalGameUsageMilli = totalGameUsageMilli + state.totalGameUsageMilli,
                        totalUsageMilli = totalTimeInMilliSeconds + state.totalUsageMilli,
                    )
                    Timber.d("posted data size is ${filteredListWithDuration.size}")
                    val mergedDaysTrackedAppsList =
                        usageOverviewUseCases.mergeDaysUsageDuration(
                            filteredListForRange,
                            usageOverviewUseCases.getDurationFromMilliseconds
                        ).sortedByDescending { it.usageDurationInMilliseconds }
                    state = state.copy(
                        trackedApps = mergedDaysTrackedAppsList
                    )
                    state.totalUsageMilli.milliseconds.toComponents { hours, minutes, _, _ ->
                        state = state.copy(
                            totalUsageDuration = hours.toInt(),
                            totalUsageMinutes = minutes
                        )

                    }
                    date = date.plusDays(1)
                }
            }

            state = state.copy(
                isLoading = false
            )
        }
        getFullyUpdatedDays()
    }

    fun isDayInUpdatedDays(timestamp: Long): Boolean {
        val today = LocalDate.now()
        val date = timestampToLocalDate(timestamp)
        return state.fullyUpdatedDays.contains(date) || (date.isBefore(today.plusDays(1)) && date.isAfter(
            today.minusDays(7)
        ))
    }
}