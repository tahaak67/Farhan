package ly.com.tahaben.notification_filter_domain.use_cases

import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import ly.com.tahaben.core.R
import ly.com.tahaben.notification_filter_domain.util.ServiceUtil
import timber.log.Timber

class OpenNotification(
    private val serviceUtil: ServiceUtil,
    private val context: Context
) {
    operator fun invoke(notificationKey: String, packageName: String) {
        val pendingIntent = serviceUtil.getNotificationIntent(notificationKey)
        Timber.d("notification click $notificationKey")
        if (pendingIntent != null) {
            val options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ActivityOptions.makeBasic()
                    .setPendingIntentBackgroundActivityStartMode(
                        // replaced by MODE_BACKGROUND_ACTIVITY_START_ALLOW_ALWAYS in API 36,
                        // but this is the only constant that exists on API 34-35
                        @Suppress("DEPRECATION")
                        ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
                    )
                    .toBundle()
            } else null
            try {
                pendingIntent.send(context, 0, null, null, null, null, options)
                Timber.d("notification click send")
            } catch (e: PendingIntent.CanceledException) {
                Timber.d(e, "pending intent was cancelled by creator app")
                openApp(packageName)
            }
        } else {
            openApp(packageName)
        }
    }

    private fun openApp(packageName: String) {
        Timber.d("notification click package $packageName")
        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.package_not_found_error),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }
}