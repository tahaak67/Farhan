package ly.com.tahaben.usage_overview_domain.use_case

data class UsageOverviewUseCases(
    val calculateUsageDuration: CalculateUsageDuration,
    val filterUsageEvents: FilterUsageEvents,
    val getDurationFromMilliseconds: GetDurationFromMilliseconds,
    val cacheUsageDataForDate: CacheUsageDataForDate,
    val getUsageDataForDate: GetUsageDataForDate,
    val isDateToDay: IsDateToday,
    val isUsagePermissionGranted: IsUsagePermissionGranted,
    val filterDuration: FilterDuration,
    val getUsageEventsFromDb: GetUsageEventsFromDb,
    val isDayDataFullyUpdated: IsDayDataFullyUpdated,
    val mergeDaysUsageDuration: MergeDaysUsageDuration,
    val getUpdatedDays: GetUpdatedDays,
    val deleteCacheForDay: DeleteCacheForDay
)
