package ly.com.tahaben.usage_overview_domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import ly.com.tahaben.usage_overview_domain.use_case.*


@Module
@InstallIn(ViewModelComponent::class)
object UsageOverviewDomainModule {

    @Provides
    @ViewModelScoped
    fun provideUsageOverviewUseCases(repository: UsageRepository): UsageOverviewUseCases {
        return UsageOverviewUseCases(
            calculateUsageDuration = CalculateUsageDuration(),
            filterUsageEvents = FilterUsageEvents(),
            getDurationFromMilliseconds = GetDurationFromMilliseconds(),
            getUsageDataForDate = GetUsageDataForDate(repository),
            isDateToDay = IsDateToday()
        )
    }
}