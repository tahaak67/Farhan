package ly.com.tahaben.infinite_scroll_blocker_data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.infinite_scroll_blocker_data.preferences.DefaultPreferences
import ly.com.tahaben.infinite_scroll_blocker_data.util.AccessibilityServiceUtilsImpl
import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences
import ly.com.tahaben.infinite_scroll_blocker_domain.util.AccessibilityServiceUtils
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InfiniteScrollBlockerPresentationModule {

    @Provides
    @Singleton
    fun provideInfiniteScrollPreferences(
        sharedPreferences: SharedPreferences,
        @ApplicationContext context: Context,
        dataStore: DataStore<androidx.datastore.preferences.core.Preferences>
    ): Preferences {
        return DefaultPreferences(sharedPreferences, context, dataStore)
    }

    @Provides
    @Singleton
    fun provideServiceTools(
        @ApplicationContext context: Context,
        preferences: Preferences
    ): AccessibilityServiceUtils {
        return AccessibilityServiceUtilsImpl(
            context,
            preferences
        )
    }

}