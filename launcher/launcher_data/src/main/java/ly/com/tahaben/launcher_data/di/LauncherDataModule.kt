package ly.com.tahaben.launcher_data.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.launcher_data.local.preferences.DefaultPreferences
import ly.com.tahaben.launcher_data.repository.AvailableActivitiesRepoImpl
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LauncherDataModule {

    @Provides
    @Singleton
    fun provideAvailableActivitiesRepo(@ApplicationContext context: Context): AvailableActivitiesRepository {
        return AvailableActivitiesRepoImpl(context)
    }

    @Provides
    @Singleton
    fun provideLauncherPreference(sharedPreferences: SharedPreferences): Preference {
        return DefaultPreferences(sharedPreferences)
    }
}