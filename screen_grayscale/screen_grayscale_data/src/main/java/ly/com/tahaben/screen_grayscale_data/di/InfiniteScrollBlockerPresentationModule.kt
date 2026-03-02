package ly.com.tahaben.screen_grayscale_data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import com.scottyab.rootbeer.RootBeer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.screen_grayscale_data.preferences.DefaultPreferences
import ly.com.tahaben.screen_grayscale_data.util.AccessibilityServiceUtilsImpl
import ly.com.tahaben.screen_grayscale_data.util.GrayscaleUtilImpl
import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences
import ly.com.tahaben.screen_grayscale_domain.util.AccessibilityServiceUtils
import ly.com.tahaben.screen_grayscale_domain.util.GrayscaleUtil
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InfiniteScrollBlockerPresentationModule {

    @Provides
    @Singleton
    fun provideInfiniteScrollPreferences(sharedPreferences: SharedPreferences, dataStore: DataStore<androidx.datastore.preferences.core.Preferences>): Preferences {
        return DefaultPreferences(sharedPreferences, dataStore)
    }

    @Provides
    @Singleton
    fun provideGrayscaleUtil(
        @ApplicationContext context: Context,
        preferences: Preferences,
        rootBeer: RootBeer
    ): GrayscaleUtil {
        return GrayscaleUtilImpl(
            context,
            preferences,
            rootBeer
        )
    }


    @Provides
    @Singleton
    fun provideAccessibilityServiceTools(
        @ApplicationContext context: Context,
        preferences: Preferences
    ): AccessibilityServiceUtils {
        return AccessibilityServiceUtilsImpl(
            context,
            preferences
        )
    }

    @Provides
    @Singleton
    fun provideRootBeer(
        @ApplicationContext context: Context
    ): RootBeer {
        return RootBeer(context)
    }

}