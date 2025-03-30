package ly.com.tahaben.launcher_domain.use_case.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build


class CheckIfCurrentLauncher(private val context: Context) {

    operator fun invoke(): Boolean {
        val pm: PackageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val res: ResolveInfo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            pm.resolveActivity(intent, 0)
        }
        return if (res?.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            false
        } else res.activityInfo.packageName == context.packageName
    }

}