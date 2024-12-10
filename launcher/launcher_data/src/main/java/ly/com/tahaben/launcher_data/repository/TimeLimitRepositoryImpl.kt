package ly.com.tahaben.launcher_data.repository

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.content.ContextCompat
import ly.com.tahaben.launcher_data.local.TimeLimitDao
import ly.com.tahaben.launcher_data.mapper.toTimeLimit
import ly.com.tahaben.launcher_data.mapper.toTimeLimitEntity
import ly.com.tahaben.launcher_data.service.TimeLimitService
import ly.com.tahaben.launcher_domain.model.TimeLimit
import ly.com.tahaben.launcher_domain.repository.TimeLimitRepository
import timber.log.Timber

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 16,Feb,2023
 */
class TimeLimitRepositoryImpl(
    private val dao: TimeLimitDao,
    private val context: Context
) : TimeLimitRepository {

    override suspend fun addAppTimeLimit(timeLimit: TimeLimit) {
        dao.addAppTimeLimit(timeLimit.toTimeLimitEntity())
    }

    override suspend fun updateAppTimeLimit(timeLimit: TimeLimit) {
        dao.updateAppTimeLimit(timeLimit.toTimeLimitEntity())
    }

    override suspend fun deleteAppTimeLimit(timeLimit: TimeLimit) {
        dao.deleteAppTimeLimit(timeLimit.toTimeLimitEntity())
    }

    override suspend fun getTimeLimitForPackage(packageName: String): TimeLimit? {
        val tl = dao.getTimeLimitForApp(packageName)?.toTimeLimit()
        Timber.d("timelimit object $tl")
        return tl
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
        } catch (e: Settings.SettingNotFoundException) {
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

    override fun launchService() {
        Intent(context, TimeLimitService::class.java).also {
            it.action = TimeLimitService.Actions.START.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Timber.d("Starting the service in >=26 Mode")
                ContextCompat.startForegroundService(context, it)
                return
            }
            Timber.d("Starting the service in < 26 Mode")
            context.startService(it)
        }
    }

    override fun stopService() {
        Intent(context, TimeLimitService::class.java).also {
            it.action = TimeLimitService.Actions.STOP.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Timber.d("Starting the service in >=26 Mode")
                ContextCompat.startForegroundService(context, it)
                return
            }
            Timber.d("Starting the service in < 26 Mode")
            context.startService(it)
        }
    }

}