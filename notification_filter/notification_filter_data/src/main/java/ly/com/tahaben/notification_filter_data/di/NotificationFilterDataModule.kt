package ly.com.tahaben.notification_filter_data.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.notification_filter_data.local.preferences.DefaultPreferences
import ly.com.tahaben.notification_filter_data.service.ServiceUtilImpl
import ly.com.tahaben.notification_filter_domain.preferences.Preferences
import ly.com.tahaben.notification_filter_domain.util.ServiceUtil
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationFilterDataModule {

    @Provides
    @Singleton
    fun provideServiceUtil(
        @ApplicationContext context: Context,
        preferences: Preferences
    ): ServiceUtil {
        return ServiceUtilImpl(context, preferences)
    }


    @Provides
    @Singleton
    fun providePreferences(sharedPreferences: SharedPreferences): Preferences {
        return DefaultPreferences(sharedPreferences)
    }
}