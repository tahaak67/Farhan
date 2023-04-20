package ly.com.tahaben.usage_overview_data.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import ly.com.tahaben.usage_overview_data.local.UsageDao
import ly.com.tahaben.usage_overview_data.local.UsageDatabase
import ly.com.tahaben.usage_overview_data.repository.UsageRepositoryImpl
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository

@Module
@InstallIn(ViewModelComponent::class)
object UsageOverviewDataModule {

    @Provides
    @ViewModelScoped
    fun provideUsageRepository(
        @ApplicationContext context: Context,
        usageDao: UsageDao
    ): UsageRepository {
        return UsageRepositoryImpl(context, usageDao)
    }

    @Provides
    @ViewModelScoped
    fun provideUsageDB(app: Application): UsageDatabase {
        return Room.databaseBuilder(app, UsageDatabase::class.java, "usage_db")
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideUsageDao(usageDB: UsageDatabase): UsageDao {
        return usageDB.dao
    }

}