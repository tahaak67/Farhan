package ly.com.tahaben.usage_overview_data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.usage_overview_data.repository.UsageRepositoryImpl
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UsageOverviewDataModule {

    @Provides
    @Singleton
    fun provideUsageRepository(@ApplicationContext context: Context): UsageRepository {
        return UsageRepositoryImpl(context)
    }


}