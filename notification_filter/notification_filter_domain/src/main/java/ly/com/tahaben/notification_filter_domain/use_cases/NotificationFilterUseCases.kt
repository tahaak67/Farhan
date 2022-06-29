package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.use_cases.settings.*

data class NotificationFilterUseCases(
    val checkIfNotificationServiceIsEnabled: CheckIfNotificationServiceIsEnabled,
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
    val setNotifyMeScheduleDate: SetNotifyMeScheduleDate
)