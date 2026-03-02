package ly.com.tahaben.farhan.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.work.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.core.service.AccessibilityServiceUtils
import ly.com.tahaben.farhan.db.DatabaseCombineHelper
import ly.com.tahaben.farhan.db.FarhanDatabase
import ly.com.tahaben.farhan.service.AccessibilityServiceUtilsImpl
import ly.com.tahaben.launcher_data.local.db.LaunchAttemptDao
import ly.com.tahaben.launcher_data.repository.AvailableActivitiesRepoImpl
import ly.com.tahaben.launcher_data.repository.TimeLimitRepositoryImpl
import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository
import ly.com.tahaben.launcher_domain.repository.LaunchAttemptsRepository
import ly.com.tahaben.launcher_domain.repository.TimeLimitRepository
import ly.com.tahaben.notification_filter_data.repositoy.NotificationRepositoryImpl
import ly.com.tahaben.notification_filter_domain.repository.NotificationRepository
import ly.com.tahaben.usage_overview_data.local.UsageDao
import ly.com.tahaben.usage_overview_domain.preferences.Preferences
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import ly.com.tahaben.usage_overview_domain.use_case.UsageOverviewUseCases
import timber.log.Timber
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
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<androidx.datastore.preferences.core.Preferences>{
        return PreferenceDataStoreFactory.create(
            produceFile = {context.preferencesDataStoreFile("farhan_data_store")}
        )
    }

    @Provides
    @Singleton
    fun provideCacheWorkerFactory(
        usageRepository: UsageRepository,
        usageOverviewUseCases: UsageOverviewUseCases,
        preferences: Preferences,
        launchAttemptsRepository: LaunchAttemptsRepository
    ): ly.com.tahaben.farhan.work_manager.DefaultWorkerFactory {
        return ly.com.tahaben.farhan.work_manager.DefaultWorkerFactory(
            usageRepository,
            usageOverviewUseCases,
            preferences,
            launchAttemptsRepository
        )
    }

    @Provides
    @Singleton
    fun provideCacheWorkerConfiguration(defaultWorkerFactory: ly.com.tahaben.farhan.work_manager.DefaultWorkerFactory): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(defaultWorkerFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
        ): FarhanDatabase{
        Timber.d("database path: ${ context.getDatabasePath("farhan_db") }")
        val farhanDatabase = Room
            .databaseBuilder(context, FarhanDatabase::class.java, "farhan_db")
            .allowMainThreadQueries()
            .build()
        return farhanDatabase
    }

    @Provides
    @Singleton
    fun provideDatabaseCombineHelper(
        @ApplicationContext context: Context,
        farhanDatabase: FarhanDatabase,
        preferences: ly.com.tahaben.domain.preferences.Preferences
    ): DatabaseCombineHelper {
        return DatabaseCombineHelper(context, farhanDatabase, preferences)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        db: FarhanDatabase
    ): NotificationRepository {
        return NotificationRepositoryImpl(db.notificationDao)
    }

    @Provides
    @Singleton
    fun provideUsageDao(db: FarhanDatabase): UsageDao {
        return db.usageDao
    }

    @Provides
    @Singleton
    fun provideAvailableActivitiesRepo(
        @ApplicationContext context: Context,
        db: FarhanDatabase
    ): AvailableActivitiesRepository {
        return AvailableActivitiesRepoImpl(context, db.appDao)
    }

    @Provides
    @Singleton
    fun provideTimeLimitRepo(
        db: FarhanDatabase,
        @ApplicationContext context: Context
    ): TimeLimitRepository {
        return TimeLimitRepositoryImpl(db.timeLimitDao, context)
    }


        @Provides
        @Singleton
        fun provideAccessibilityUtils(
            @ApplicationContext context: Context,
            sharedPref: ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences
        ): AccessibilityServiceUtils {
            return AccessibilityServiceUtilsImpl(context, sharedPref)
        }

    @Provides
    @Singleton
    fun provideLaunchAttemptDao(db: FarhanDatabase): LaunchAttemptDao {
      return db.launchAttemptDao
    }
}