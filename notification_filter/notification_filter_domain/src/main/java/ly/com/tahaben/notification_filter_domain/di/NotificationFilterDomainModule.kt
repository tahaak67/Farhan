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
import ly.com.tahaben.notification_filter_domain.use_cases.AddPackageToNotificationException
import ly.com.tahaben.notification_filter_domain.use_cases.CheckIfNotificationAccessIsGranted
import ly.com.tahaben.notification_filter_domain.use_cases.CheckIfNotificationServiceIsEnabled
import ly.com.tahaben.notification_filter_domain.use_cases.CreateNotifyMeNotificationChannel
import ly.com.tahaben.notification_filter_domain.use_cases.DeleteAllNotifications
import ly.com.tahaben.notification_filter_domain.use_cases.DeleteNotificationFromDB
import ly.com.tahaben.notification_filter_domain.use_cases.DeleteNotificationIntentFromHashmap
import ly.com.tahaben.notification_filter_domain.use_cases.EnableNotificationService
import ly.com.tahaben.notification_filter_domain.use_cases.GetInstalledAppsList
import ly.com.tahaben.notification_filter_domain.use_cases.GetNotificationsFromDB
import ly.com.tahaben.notification_filter_domain.use_cases.InsertNotificationToDB
import ly.com.tahaben.notification_filter_domain.use_cases.IsCurrentTimeWithinFilterSchedule
import ly.com.tahaben.notification_filter_domain.use_cases.IsPackageInNotificationException
import ly.com.tahaben.notification_filter_domain.use_cases.LoadShouldShowOnBoarding
import ly.com.tahaben.notification_filter_domain.use_cases.NotificationFilterUseCases
import ly.com.tahaben.notification_filter_domain.use_cases.OpenNotification
import ly.com.tahaben.notification_filter_domain.use_cases.RemovePackageFromNotificationException
import ly.com.tahaben.notification_filter_domain.use_cases.SaveShouldShowOnBoarding
import ly.com.tahaben.notification_filter_domain.use_cases.ScheduleNotifyMeNotification
import ly.com.tahaben.notification_filter_domain.use_cases.StartNotificationService
import ly.com.tahaben.notification_filter_domain.use_cases.settings.CanScheduleExactAlarms
import ly.com.tahaben.notification_filter_domain.use_cases.settings.GetFilterSchedule
import ly.com.tahaben.notification_filter_domain.use_cases.settings.GetNotifyMeHour
import ly.com.tahaben.notification_filter_domain.use_cases.settings.GetNotifyMeMinute
import ly.com.tahaben.notification_filter_domain.use_cases.settings.OpenExactAlarmsPermissionScreen
import ly.com.tahaben.notification_filter_domain.use_cases.settings.OpenSettings
import ly.com.tahaben.notification_filter_domain.use_cases.settings.SetFilterScheduleDays
import ly.com.tahaben.notification_filter_domain.use_cases.settings.SetFilterScheduleEnabled
import ly.com.tahaben.notification_filter_domain.use_cases.settings.SetFilterScheduleEndTime
import ly.com.tahaben.notification_filter_domain.use_cases.settings.SetFilterScheduleStartTime
import ly.com.tahaben.notification_filter_domain.use_cases.settings.SetNotifyMeScheduleDate
import ly.com.tahaben.notification_filter_domain.use_cases.settings.SetNotifyMeTime
import ly.com.tahaben.notification_filter_domain.use_cases.settings.SetServiceState
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
            CheckIfNotificationAccessIsGranted(serviceUtil),
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
            SetNotifyMeScheduleDate(preferences),
            SaveShouldShowOnBoarding(preferences),
            LoadShouldShowOnBoarding(preferences),
            OpenSettings(serviceUtil),
            CanScheduleExactAlarms(serviceUtil),
            OpenExactAlarmsPermissionScreen(serviceUtil),
            GetFilterSchedule(preferences),
            SetFilterScheduleEnabled(preferences),
            SetFilterScheduleDays(preferences),
            SetFilterScheduleStartTime(preferences),
            SetFilterScheduleEndTime(preferences),
            IsCurrentTimeWithinFilterSchedule(preferences)
        )
    }

}