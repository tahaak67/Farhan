package ly.com.tahaben.launcher_data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository
import timber.log.Timber


class AvailableActivitiesRepoImpl(
    private val context: Context
) : AvailableActivitiesRepository {

    override fun getActivities(): List<AppItem> {
        val pm: PackageManager =
            context.packageManager
        val appsList = ArrayList<AppItem>()

        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = pm.queryIntentActivities(i, 0)
        for (ri in allApps) {
            val app = AppItem(
                name = ri.loadLabel(pm).toString(),
                packageName = ri.activityInfo.packageName,
            )
            appsList.add(app)
        }
        return appsList
    }

    override fun launchMainActivityFor(app: AppItem) {
        val launchIntent =
            context.packageManager.getLaunchIntentForPackage(app.packageName)
        launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
    }

    override fun launchAppInfo(app: AppItem) {
        val appInfoIntent = Intent()
        appInfoIntent.action =
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        appInfoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appInfoIntent.data =
            Uri.fromParts("package", app.packageName, null)
        context.startActivity(appInfoIntent)
    }

    override fun launchDefaultDialerApp() {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_DIAL)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val componentName = intent.resolveActivity(pm)
        if (componentName == null) context.startActivity(intent) else
            pm.getLaunchIntentForPackage(componentName.packageName)?.let {
                context.startActivity(it)
            } ?: run { context.startActivity(intent) }
    }

    override fun launchDefaultCameraApp() {
        val dialerIntent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        dialerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(dialerIntent)
    }

    override fun launchDefaultAlarmApp() {
        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}