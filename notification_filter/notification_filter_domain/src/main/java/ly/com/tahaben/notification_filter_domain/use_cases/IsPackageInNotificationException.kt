package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.preferences.Preferences

class IsPackageInNotificationException(
    private val sharedPref: Preferences
) {
    operator fun invoke(packageName: String): Boolean {
        return sharedPref.isPackageInNotificationExceptions(packageName)
    }
}