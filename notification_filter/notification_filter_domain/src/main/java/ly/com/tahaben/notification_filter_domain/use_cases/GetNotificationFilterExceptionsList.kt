package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.preferences.Preferences

class GetNotificationFilterExceptionsList(
    private val sharedPref: Preferences
) {
    operator fun invoke(): Set<String> {
        return sharedPref.getNotificationFilterExceptionsList()
    }
}