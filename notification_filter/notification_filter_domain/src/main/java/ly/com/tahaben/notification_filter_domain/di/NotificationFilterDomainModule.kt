package ly.com.tahaben.notification_filter_domain.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.notification_filter_domain.preferences.Preferences
import ly.com.tahaben.notification_filter_domain.repository.NotificationRepository
import ly.com.tahaben.notification_filter_domain.use_cases.*
import ly.com.tahaben.notification_filter_domain.use_cases.settings.*
import ly.com.tahaben.notification_filter_domain.util.ServiceUtil
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NotificationFilterDomainModule {

    @Provides
    @Singleton
    fun provideNotificationUseCases(
        notificationRepository: NotificationRepository,
        serviceUtil: ServiceUtil,
        @ApplicationContext context: Context,
        preferences: Preferences,
        installedAppsRepo: InstalledAppsRepository
    ): NotificationFilterUseCases {
        return NotificationFilterUseCases(
            CheckIfNotificationServiceIsEnabled(serviceUtil),
            DeleteNotificationFromDB(notificationRepository),
            DeleteNotificationIntentFromHashmap(serviceUtil),
            DeleteAllNotifications(notificationRepository),
            EnableNotificationService(serviceUtil),
            InsertNotificationToDB(notificationRepository),
            GetNotificationsFromDB(notificationRepository),
            StartNotificationService(serviceUtil),
            OpenNotification(serviceUtil, context),
            IsPackageInNotificationException(preferences),
            GetInstalledAppsList(installedAppsRepo),
            AddPackageToNotificationException(preferences),
            RemovePackageFromNotificationException(preferences),
            GetNotifyMeHour(preferences),
            GetNotifyMeMinute(preferences),
            SetNotifyMeTime(preferences),
            SetServiceState(preferences),
            ScheduleNotifyMeNotification(serviceUtil),
            CreateNotifyMeNotificationChannel(serviceUtil),
            SetNotifyMeScheduleDate(preferences)
        )
    }

}