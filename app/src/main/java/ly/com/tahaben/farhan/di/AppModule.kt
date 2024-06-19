package ly.com.tahaben.farhan.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.work.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.usage_overview_domain.preferences.Preferences
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import ly.com.tahaben.usage_overview_domain.use_case.UsageOverviewUseCases
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        app: Application
    ): SharedPreferences {
        return app.getSharedPreferences("shared_pref", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideCacheWorkerFactory(
        usageRepository: UsageRepository,
        usageOverviewUseCases: UsageOverviewUseCases,
        preferences: Preferences
    ): ly.com.tahaben.farhan.work_manager.DefaultWorkerFactory {
        return ly.com.tahaben.farhan.work_manager.DefaultWorkerFactory(
            usageRepository,
            usageOverviewUseCases,
            preferences
        )
    }

    @Provides
    @Singleton
    fun provideCacheWorkerConfiguration(defaultWorkerFactory: ly.com.tahaben.farhan.work_manager.DefaultWorkerFactory): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(defaultWorkerFactory)
            .build()
    }
}