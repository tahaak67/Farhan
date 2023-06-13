package ly.com.tahaben.usage_overview_domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import ly.com.tahaben.usage_overview_domain.preferences.Preferences
import ly.com.tahaben.usage_overview_domain.repository.WorkerRepository
import ly.com.tahaben.usage_overview_domain.use_case.*

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 15,May,2023
 */
@Module
@InstallIn(ViewModelComponent::class)
object UsageSettingsDomainModule {

    @Provides
    @ViewModelScoped
    fun provideUsageSettingsUseCases(
        workerRepository: WorkerRepository,
        preferences: Preferences
    ): UsageSettingsUseCases {
        return UsageSettingsUseCases(
            setAutoCachingEnabled = SetAutoCachingEnabled(workerRepository),
            isAutoCachingEnabled = IsAutoCachingEnabled(preferences),
            isCachingEnabled = IsCachingEnabled(preferences),
            setCachingEnabled = SetCachingEnabled(preferences),
            openAppSettings = OpenAppSettings(workerRepository),
            setUsageReportsEnabled = SetUsageReportsEnabled(workerRepository),
            getEnabledUsageReports = GetEnabledUsageReports(preferences)
        )
    }
}