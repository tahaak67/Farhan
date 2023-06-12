package ly.com.tahaben.usage_overview_domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import ly.com.tahaben.usage_overview_domain.use_case.*
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
            getFullyUpdatedDays = GetFullyUpdatedDays(usageRepository),
            deleteCacheForDay = DeleteCacheForDay(usageRepository)
        )
    }
}