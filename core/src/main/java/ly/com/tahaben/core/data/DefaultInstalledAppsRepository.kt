package ly.com.tahaben.core.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.core.model.AppItem
import timber.log.Timber

class DefaultInstalledAppsRepository(
    private val context: Context
) : InstalledAppsRepository {

    @SuppressLint("QueryPermissionsNeeded")
    override suspend fun getInstalledApps(): List<AppItem> {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(0)
        val appItemList = arrayListOf<AppItem>()
        val isSystemAppMask = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            apps.forEach { ai ->
                val isSys = (ai.flags and isSystemAppMask) != 0
                Timber.d("app: ${ai.packageName} isSys: ${isSys}")
                appItemList.add(
                    AppItem(
                        name = ai.loadLabel(pm) as String,
                        pckg = ai.packageName,
                        category = ApplicationInfo.getCategoryTitle(context, ai.category)
                            ?.toString(),
                        isSystemApp = isSys
                    )
                )
            }
        } else {
            apps.forEach { ai ->
                appItemList.add(
                    AppItem(
                        ai.loadLabel(pm) as String,
                        ai.packageName,
                        isSystemApp = (ai.flags + isSystemAppMask) != 0
                    )
                )
            }
        }
        return appItemList
    }
}