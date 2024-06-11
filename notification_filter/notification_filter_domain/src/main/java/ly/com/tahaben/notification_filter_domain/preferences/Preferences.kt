package ly.com.tahaben.notification_filter_domain.preferences

interface Preferences {

    fun loadShouldShowOnBoarding(): Boolean
    fun saveShouldShowOnBoarding(shouldShow: Boolean)
    fun loadShouldShowcase(): Boolean
    fun saveShouldShowcase(shouldShow: Boolean)
    fun isServiceEnabled(): Boolean
    fun setServiceState(isEnabled: Boolean)
    fun savePackageToNotificationExceptions(packageName: String)
    fun removePackageFromNotificationExceptions(packageName: String)
    fun isPackageInNotificationExceptions(packageName: String): Boolean
    fun getNotificationFilterExceptionsList(): Set<String>
    fun setNotifyMeTime(hour: Int, minutes: Int)
    fun isNotifyMeScheduledToday(): Boolean
    fun setNotifyMeScheduledDate(date: Long)
    fun getNotifyMeHours(): Int
    fun getNotifyMeMinutes(): Int
    fun getSettingsShouldShowWarning(): Boolean
    fun setSettingsShouldShowWarning(shouldShow: Boolean)


    companion object {
        const val KEY_NOTIFICATION_SERVICE_STATS = "notification_service_stats"
        const val KEY_NOTIFICATION_FILTER_EXCEPTIONS = "notification_filter_exceptions"
        const val KEY_NOTIFY_ME_SCHEDULE_DATE = "notify_me_schedule_date"
        const val KEY_NOTIFY_ME_HOUR = "notify_me_hour"
        const val KEY_NOTIFY_ME_MINUTE = "notify_me_minute"
        const val KEY_NOTIFICATION_FILTER_SHOULD_SHOW_ON_BOARDING =
            "notification_filter_should_show_on_boarding"
        const val KEY_NOTIFICATION_FILTER_SHOULD_SHOWCASE =
            "notification_filter_should_showcase"
        const val KEY_NOTIFICATION_FILTER_WARNING = "notification_filter_settings_warning"
    }
}