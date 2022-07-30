package ly.com.tahaben.usage_overview_presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.use_case.UsageOverviewUseCases
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

    private var getUsageDataJob: Job? = null


    fun checkUsagePermissionState() {
        state = state.copy(
            isUsagePermissionGranted = usageOverviewUseCases.isUsagePermissionGranted()
        )
        if (state.isUsagePermissionGranted) {
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
                totalUsageMilli = 1,
                totalSocialUsageMilli = 0,
                totalProductivityUsageMilli = 0,
                totalGameUsageMilli = 0,
                totalUsageMinutes = 0,
                trackedApps = emptyList(),
                isDateToday = usageOverviewUseCases.isDateToDay(state.date)
            )
            getUsageDataJob?.cancel()
            getUsageDataJob = usageOverviewUseCases.getUsageDataForDate(state.date)
                .onEach { usageDataList ->
                    val filteredList = usageOverviewUseCases.filterUsageEvents(usageDataList)
                    val filteredListWithDuration = usageOverviewUseCases.calculateUsageDuration(
                        filteredList,
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
                            isLoading = false,
                            trackedApps = filteredListWithDuration
                        )
                    }
                }
                .launchIn(this)
        }
    }

    fun onEvent(event: UsageOverviewEvent) {
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
        }
    }
}