package ly.com.tahaben.launcher_data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.launcher_data.local.db.LaunchAttemptDao
import ly.com.tahaben.launcher_data.local.preferences.DefaultPreferences
import ly.com.tahaben.launcher_data.repository.LaunchAttemptsRepoImpl
import ly.com.tahaben.launcher_data.repository.WorkerRepoImpl
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.launcher_domain.repository.LaunchAttemptsRepository
import ly.com.tahaben.launcher_domain.repository.WorkerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LauncherDataModule {


    @Provides
    @Singleton
    fun provideLauncherPreference(
        sharedPreferences: SharedPreferences,
        dataStore: DataStore<Preferences>,
        @ApplicationContext context: Context
    ): Preference {
        return DefaultPreferences(sharedPreferences, dataStore, context)
    }

    @Provides
    @Singleton
    fun provideLaunchAttemptsRepository(
        dao: LaunchAttemptDao
    ): LaunchAttemptsRepository {
        return LaunchAttemptsRepoImpl(dao)
    }

    @Provides
    @Singleton
    fun provideLauncherWorkerRepo(workManager: WorkManager): WorkerRepository {
      return WorkerRepoImpl(workManager)
    }
}