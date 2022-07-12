package ly.com.tahaben.screen_grayscale_data.util


import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences
import ly.com.tahaben.screen_grayscale_domain.util.AccessibilityServiceUtils
import timber.log.Timber

class AccessibilityServiceUtilsImpl(
    private val context: Context,
    private val sharedPref: Preferences
) : AccessibilityServiceUtils {

    override fun checkIfServiceEnabled(): Boolean {
        return sharedPref.isGrayscaleEnabled()
    }

    override fun askForAccessibilityPermission() {
        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    override fun checkIfAccessibilityPermissionGranted(): Boolean {
        var accessibilityEnabled = 0
        val service: String =
            context.packageName + "/" + "ly.com.tahaben.farhan.service.AccessibilityService"
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            Timber.v("accessibilityEnabled = $accessibilityEnabled")
        } catch (e: SettingNotFoundException) {
            Timber.e(
                "Error finding setting, default accessibility to not found: "
                        + e.message
            )
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            Timber.v("***ACCESSIBILITY IS ENABLED*** -----------------")
            val settingValue = Settings.Secure.getString(
                context.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService: String = mStringColonSplitter.next()
                    Timber.v(
                        "-------------- > accessibilityService :: $accessibilityService $service"
                    )
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        Timber.v(
                            "We've found the correct setting - accessibility is switched on!"
                        )
                        return true
                    }
                }
            }
        } else {
            Timber.v("***ACCESSIBILITY IS DISABLED***")
        }
        return false
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