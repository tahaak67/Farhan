package ly.com.tahaben.usage_overview_presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.model.UsageDurationDataItem
import ly.com.tahaben.usage_overview_domain.use_case.UsageOverviewUseCases
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class UsageOverviewViewModel @Inject constructor(
    private val usageOverviewUseCases: UsageOverviewUseCases
) : ViewModel() {


    var state by mutableStateOf(UsageOverviewState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        getFullyUpdatedDays()
    }

    private fun getFullyUpdatedDays() {
        viewModelScope.launch {
            state = state.copy(
                fullyUpdatedDays = usageOverviewUseCases.getFullyUpdatedDays()
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

    fun askForUsagePermission() {
        refreshUsageData()
    }

    private fun refreshUsageData() {
        viewModelScope.launch {
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
            if (!state.fullyUpdatedDays.contains(state.date)) {
                usageOverviewUseCases.getUsageDataForDate(state.date)
            }
            val usageDataList = usageOverviewUseCases.returnUsageEvents(state.date)
            val filteredList = usageOverviewUseCases.filterUsageEvents(usageDataList)
            val filteredListWithDuration = usageOverviewUseCases.calculateUsageDuration(
                if (state.isDateToday) filteredList.dropLast(1) else filteredList,
                usageOverviewUseCases.getDurationFromMilliseconds,
                usageOverviewUseCases.filterDuration
            ).sortedByDescending { it.usageDurationInMilliseconds }
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
                            isModeRange = true
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
        viewModelScope.launch {
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
                    if (!state.fullyUpdatedDays.contains(startDate)) {
                        usageOverviewUseCases.getUsageDataForDate(startDate)
                    }
                    startDate = startDate.plusDays(1)
                }
            }
            // calculation
            var date = state.rangeStartDate
            if (date != null) {
                val filteredListForRange = mutableListOf<UsageDurationDataItem>()
                while (date!! <= state.rangeEndDate) {
                    val usageDataList = usageOverviewUseCases.returnUsageEvents(date)

                    val filteredList =
                        usageOverviewUseCases.filterUsageEvents(usageDataList)
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