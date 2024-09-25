package ly.com.tahaben.usage_overview_data.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.usage_overview_data.local.UsageDao
import ly.com.tahaben.usage_overview_data.preferences.DefaultPreferences
import ly.com.tahaben.usage_overview_data.repository.UsageRepositoryImpl
import ly.com.tahaben.usage_overview_data.repository.WorkerRepoImpl
import ly.com.tahaben.usage_overview_domain.preferences.Preferences
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import ly.com.tahaben.usage_overview_domain.repository.WorkerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UsageOverviewDataModule {

    @Provides
    @Singleton
    fun provideWorkerRepository(
        workManager: WorkManager,
        preferences: Preferences,
        app: Application
    ): WorkerRepository {
        return WorkerRepoImpl(workManager, preferences, app)
    }

    @Provides
    @Singleton
    fun provideUsageRepository(
        @ApplicationContext context: Context,
        usageDao: UsageDao
    ): UsageRepository {
        return UsageRepositoryImpl(context, usageDao)
    }

    @Provides
    @Singleton
    fun provideWorkManager(app: Application): WorkManager {
        return WorkManager.getInstance(app)
    }

    @Provides
    @Singleton
    fun provideUsagePreferences(sharedPreferences: SharedPreferences): Preferences {
        return DefaultPreferences(sharedPreferences)
    }

}