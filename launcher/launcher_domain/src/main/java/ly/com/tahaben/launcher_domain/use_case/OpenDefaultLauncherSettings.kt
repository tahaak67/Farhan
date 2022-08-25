package ly.com.tahaben.launcher_domain.use_case

import android.content.Context
import android.content.Intent
import android.provider.Settings

class OpenDefaultLauncherSettings(
    private val context: Context
) {
    operator fun invoke() {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}