package ly.com.tahaben.infinite_scroll_blocker_data.util


import android.content.Context
import android.content.Intent
import android.provider.Settings
import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences
import ly.com.tahaben.infinite_scroll_blocker_domain.util.AccessibilityServiceUtils
import timber.log.Timber

class AccessibilityServiceUtilsImpl(
    private val context: Context,
    private val sharedPref: Preferences
) : AccessibilityServiceUtils {

    override fun checkIfServiceEnabled(): Boolean {
        Timber.d("service start")
        return if (sharedPref.isServiceEnabled()) {
            checkIfAccessibilityPermissionGranted()
        } else {
            false
        }
    }

    override fun askForAccessibilityPermission() {
        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    override fun checkIfAccessibilityPermissionGranted(): Boolean {
        val contentResolver = context.contentResolver
        val accessibilityServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ).orEmpty()
        val ACCESSIBILITY_SERVICE_COMPONENT =
            context.packageName + "/" + "ly.com.tahaben.farhan.service.AccessibilityService"
        val accessibilityServiceIsActivated =
            accessibilityServices.split(":").contains(ACCESSIBILITY_SERVICE_COMPONENT)

        /* if (context.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS")
             == PackageManager.PERMISSION_GRANTED) {
             // we can (de-)activate the accessibility service programmatically, so let's do so
             val aServices = Settings.Secure.getString(
                 contentResolver,
                 Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
             ).orEmpty()
             if (!accessibilityServiceIsActivated) {
                 Timber.d("service not active !")
                 Settings.Secure.putString(
                     contentResolver,
                     Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                     "$aServices:$ACCESSIBILITY_SERVICE_COMPONENT".trim(':')
                 )
                 Settings.Secure.putString(
                     contentResolver,
                     Settings.Secure.ACCESSIBILITY_ENABLED,
                     "1"
                 )
             } else if (accessibilityServiceIsActivated) {
                 Settings.Secure.putString(
                     contentResolver,
                     Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                     accessibilityServices.replace(ACCESSIBILITY_SERVICE_COMPONENT, "").trim(':')
                 )
             }
             return true
         } else if (!accessibilityServiceIsActivated) {
             // we need to ask the user to activate the accessibility service manually
             return false
         }*/
        return accessibilityServiceIsActivated
    }

    override fun checkIfAppearOnTopPermissionGranted(): Boolean {
        val t = Settings.canDrawOverlays(context)
        Timber.d("is granted? $t")
        return t
    }

    override fun askForAppearOnTopPermission() {
        context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}