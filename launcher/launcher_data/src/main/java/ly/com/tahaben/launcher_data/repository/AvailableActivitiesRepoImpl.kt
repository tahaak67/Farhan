package ly.com.tahaben.launcher_data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository


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
}