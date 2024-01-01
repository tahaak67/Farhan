package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.use_cases.settings.CanScheduleExactAlarms
import ly.com.tahaben.notification_filter_domain.use_cases.settings.GetNotifyMeHour
import ly.com.tahaben.notification_filter_domain.use_cases.settings.GetNotifyMeMinute
import ly.com.tahaben.notification_filter_domain.use_cases.settings.OpenExactAlarmsPermissionScreen
import ly.com.tahaben.notification_filter_domain.use_cases.settings.OpenSettings
import ly.com.tahaben.notification_filter_domain.use_cases.settings.SetNotifyMeScheduleDate
import ly.com.tahaben.notification_filter_domain.use_cases.settings.SetNotifyMeTime
import ly.com.tahaben.notification_filter_domain.use_cases.settings.SetServiceState

data class NotificationFilterUseCases(
    val checkIfNotificationServiceIsEnabled: CheckIfNotificationServiceIsEnabled,
    val checkIfNotificationAccessIsGranted: CheckIfNotificationAccessIsGranted,
    val deleteNotificationFromDB: DeleteNotificationFromDB,
    val deleteNotificationIntentFromHashmap: DeleteNotificationIntentFromHashmap,
    val deleteAllNotifications: DeleteAllNotifications,
    val enableNotificationService: EnableNotificationService,
    val insertNotificationToDB: InsertNotificationToDB,
    val getNotificationsFromDB: GetNotificationsFromDB,
    val startNotificationService: StartNotificationService,
    val openNotification: OpenNotification,
    val isPackageInNotificationException: IsPackageInNotificationException,
    val getInstalledAppsList: GetInstalledAppsList,
    val addPackageToNotificationException: AddPackageToNotificationException,
    val removePackageFromNotificationException: RemovePackageFromNotificationException,
    val getNotifyMeHour: GetNotifyMeHour,
    val getNotifyMeMinute: GetNotifyMeMinute,
    val setNotifyMeTime: SetNotifyMeTime,
    val setServiceState: SetServiceState,
    val scheduleNotifyMeNotification: ScheduleNotifyMeNotification,
    val createNotifyMeNotificationChannel: CreateNotifyMeNotificationChannel,
    val setNotifyMeScheduleDate: SetNotifyMeScheduleDate,
    val saveShouldShowOnBoarding: SaveShouldShowOnBoarding,
    val loadShouldShowOnBoarding: LoadShouldShowOnBoarding,
    val openAppSettings: OpenSettings,
    val canScheduleExactAlarms: CanScheduleExactAlarms,
    val openExactAlarmsPermissionScreen: OpenExactAlarmsPermissionScreen
)