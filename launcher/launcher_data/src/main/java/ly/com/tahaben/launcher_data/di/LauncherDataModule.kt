package ly.com.tahaben.launcher_data.di

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.launcher_data.local.preferences.DefaultPreferences
import ly.com.tahaben.launcher_domain.preferences.Preference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LauncherDataModule {


    @Provides
    @Singleton
    fun provideLauncherPreference(sharedPreferences: SharedPreferences, dataStore: DataStore<Preferences>): Preference {
        return DefaultPreferences(sharedPreferences, dataStore)
    }

}