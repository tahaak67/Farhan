package ly.com.tahaben.launcher_domain.use_case

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import timber.log.Timber


class CheckIfCurrentLauncher(private val context: Context) {

    operator fun invoke(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val res: ResolveInfo? = context.packageManager.resolveActivity(intent, 0)
        return if (res?.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            false
        } else if ("android".equals(res.activityInfo.packageName)) {
            // No default selected
            false
        } else {
            // res.activityInfo.packageName and res.activityInfo.name gives you the default app
            Timber.d(res.activityInfo.packageName)
            res.activityInfo.packageName == context.packageName
        }
    }

}