package ly.com.tahaben.usage_overview_presentation

import ly.com.tahaben.usage_overview_domain.model.UsageDurationDataItem
import java.time.LocalDate
import java.time.Year

data class UsageOverviewState(
    val totalSocialUsageMilli: Long = 0,
    val totalProductivityUsageMilli: Long = 0,
    val totalGameUsageMilli: Long = 0,
    val totalUsageMilli: Long = 0,
    val totalUsageDuration: Int = 0,
    val totalUsageMinutes: Int = 0,
    val date: LocalDate = LocalDate.now(),
    val trackedApps: List<UsageDurationDataItem> = emptyList(),
    val isLoading: Boolean = false,
    val isDateToday: Boolean = true,
    val isUsagePermissionGranted: Boolean = true,
    val isModeRange: Boolean = false,
    val rangeStartDate: LocalDate? = null,
    val rangeEndDate: LocalDate? = null,
    val isDropDownMenuVisible: Boolean = false,
    val fullyUpdatedDays: List<LocalDate> = emptyList(),
    val isDeleteDialogVisible: Boolean = false,
    val currentYear: Int = Year.now().value
)
