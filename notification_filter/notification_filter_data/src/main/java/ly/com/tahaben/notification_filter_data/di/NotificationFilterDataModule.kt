package ly.com.tahaben.notification_filter_data.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.notification_filter_data.local.NotificationDatabase
import ly.com.tahaben.notification_filter_data.local.preferences.DefaultPreferences
import ly.com.tahaben.notification_filter_data.repositoy.NotificationRepositoryImpl
import ly.com.tahaben.notification_filter_data.service.ServiceUtilImpl
import ly.com.tahaben.notification_filter_domain.preferences.Preferences
import ly.com.tahaben.notification_filter_domain.repository.NotificationRepository
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
    fun provideNotificationDatabase(app: Application): NotificationDatabase {
        return Room.databaseBuilder(
            app,
            NotificationDatabase::class.java,
            "notifications_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        db: NotificationDatabase
    ): NotificationRepository {
        return NotificationRepositoryImpl(db.dao)
    }

    @Provides
    @Singleton
    fun providePreferences(sharedPreferences: SharedPreferences): Preferences {
        return DefaultPreferences(sharedPreferences)
    }
}