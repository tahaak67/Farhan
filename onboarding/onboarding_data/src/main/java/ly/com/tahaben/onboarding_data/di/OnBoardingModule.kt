package ly.com.tahaben.onboarding_data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.domain.preferences.Preferences
import ly.com.tahaben.onboarding_data.preferences.DefaultPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnBoardingModule {

    @Provides
    @Singleton
    fun provideInfiniteScrollPreferences(
        sharedPreferences: SharedPreferences,
        @ApplicationContext context: Context,
        dataStore: DataStore<androidx.datastore.preferences.core.Preferences>
    ): Preferences {
        return DefaultPreferences(sharedPreferences, context, dataStore)
    }
}

