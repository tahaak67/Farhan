package ly.com.tahaben.usage_overview_domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import ly.com.tahaben.usage_overview_domain.use_case.CacheUsageDataForDate
import ly.com.tahaben.usage_overview_domain.use_case.CalculateUsageDuration
import ly.com.tahaben.usage_overview_domain.use_case.DeleteCacheForDay
import ly.com.tahaben.usage_overview_domain.use_case.FilterDuration
import ly.com.tahaben.usage_overview_domain.use_case.FilterUsageEvents
import ly.com.tahaben.usage_overview_domain.use_case.GetDurationFromMilliseconds
import ly.com.tahaben.usage_overview_domain.use_case.GetUpdatedDays
import ly.com.tahaben.usage_overview_domain.use_case.GetUsageDataForDate
import ly.com.tahaben.usage_overview_domain.use_case.GetUsageEventsFromDb
import ly.com.tahaben.usage_overview_domain.use_case.IsDateToday
import ly.com.tahaben.usage_overview_domain.use_case.IsDayDataFullyUpdated
import ly.com.tahaben.usage_overview_domain.use_case.IsDayOver
import ly.com.tahaben.usage_overview_domain.use_case.IsUsagePermissionGranted
import ly.com.tahaben.usage_overview_domain.use_case.MergeDaysUsageDuration
import ly.com.tahaben.usage_overview_domain.use_case.UsageOverviewUseCases
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UsageOverviewDomainModule {

    @Provides
    @Singleton
    fun provideUsageOverviewUseCases(
        usageRepository: UsageRepository
    ): UsageOverviewUseCases {
        return UsageOverviewUseCases(
            calculateUsageDuration = CalculateUsageDuration(),
            filterUsageEvents = FilterUsageEvents(),
            getDurationFromMilliseconds = GetDurationFromMilliseconds(),
            cacheUsageDataForDate = CacheUsageDataForDate(usageRepository),
            getUsageDataForDate = GetUsageDataForDate(usageRepository),
            isDateToDay = IsDateToday(),
            isUsagePermissionGranted = IsUsagePermissionGranted(usageRepository),
            filterDuration = FilterDuration(),
            getUsageEventsFromDb = GetUsageEventsFromDb(usageRepository),
            isDayDataFullyUpdated = IsDayDataFullyUpdated(usageRepository),
            mergeDaysUsageDuration = MergeDaysUsageDuration(),
            getUpdatedDays = GetUpdatedDays(usageRepository),
            deleteCacheForDay = DeleteCacheForDay(usageRepository),
            isDayOver = IsDayOver()
        )
    }
}