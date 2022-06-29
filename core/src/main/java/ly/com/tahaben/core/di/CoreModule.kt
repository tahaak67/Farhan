package ly.com.tahaben.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.core.data.DefaultInstalledAppsRepository
import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideInstalledAppsRepository(
        @ApplicationContext context: Context
    ): InstalledAppsRepository {
        return DefaultInstalledAppsRepository(context)
    }
}