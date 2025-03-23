package ly.com.tahaben.launcher_data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.net.Uri
import android.os.Process
import android.os.UserManager
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.launcher_data.local.AppsDao
import ly.com.tahaben.launcher_data.mapper.toAppEntity
import ly.com.tahaben.launcher_data.mapper.toAppItem
import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository


class AvailableActivitiesRepoImpl(
    private val context: Context,
    private val dao: AppsDao
) : AvailableActivitiesRepository {

    override suspend fun getActivities(): List<AppItem> {
        val manager: UserManager = context.getSystemService(Context.USER_SERVICE) as UserManager
        val launcher: LauncherApps =
            context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val myUserHandle = Process.myUserHandle()

        val appsList = ArrayList<AppItem>()

        for (profile in manager.userProfiles) {
            val prefix = if (profile == myUserHandle) "" else "\uD83C\uDD46 " //Unicode for boxed w
            val profileSerial = manager.getSerialNumberForUser(profile)

            for (activityInfo in launcher.getActivityList(null, profile)) {
                val app = AppItem(
                    name = prefix + activityInfo.label.toString(),
                    packageName = activityInfo.applicationInfo.packageName,
                    activityName = activityInfo.name,
                    userSerial = profileSerial
                )
                appsList.add(app)
                dao.insertApp(app.toAppEntity())
            }
        }
        return appsList

        /* val i = Intent(Intent.ACTION_MAIN, null)
         i.addCategory(Intent.CATEGORY_LAUNCHER)

         val allApps = pm.queryIntentActivities(i, 0)
         for (ri in allApps) {
             ri.activityInfo
             val app = AppItem(
                 name = ri.loadLabel(pm).toString(),
                 packageName = ri.activityInfo.packageName,
             )
             appsList.add(app)
         }*/
    }

    override suspend fun deleteActivity(appItem: AppItem) {
        dao.removeDeletedApp(appItem.toAppEntity())
    }

    override fun loadActivitiesFromDb(): Flow<List<AppItem>> {
        return dao.getInstalledActivities().map { entities ->
            entities.map { it.toAppItem() }
        }
    }

    override fun launchActivity(app: AppItem) {
        val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
        val launcher = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val component = ComponentName(app.packageName, app.activityName ?: "")
        val userHandler = userManager.getUserForSerialNumber(app.userSerial)

        launcher.startMainActivity(component, userHandler, null, null)
        /*val launchIntent =
            context.packageManager.getLaunchIntentForPackage(app.packageName)
        launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)*/
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