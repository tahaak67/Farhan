package ly.com.tahaben.onboarding_data.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.domain.preferences.Preferences
import ly.com.tahaben.onboarding_data.preferences.DefaultPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnBoardingModule {

    @Provides
    @Singleton
    fun provideInfiniteScrollPreferences(sharedPreferences: SharedPreferences): Preferences {
        return DefaultPreferences(sharedPreferences)
    }
}

