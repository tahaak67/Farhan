package ly.com.tahaben.farhan

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.auto.service.AutoService
import org.acra.ReportField
import org.acra.config.CoreConfiguration
import org.acra.data.CrashReportData
import org.acra.sender.ReportSender
import org.acra.sender.ReportSenderFactory
import java.io.File

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 7/4/2025.
 */

@AutoService(ReportSenderFactory::class)
class LocalReportSenderFactory : ReportSenderFactory {
    override fun create(context: Context, config: CoreConfiguration): ReportSender {
        return object : ReportSender {
            override fun send(context: Context, errorContent: CrashReportData) {
                // 1. Save crash to file
                val stackTrace = errorContent.getString(ReportField.STACK_TRACE)
                val appVersionName = errorContent.getString(ReportField.APP_VERSION_NAME)
                val appVersionCode = errorContent.getString(ReportField.APP_VERSION_CODE)
                val androidVersion = errorContent.getString(ReportField.ANDROID_VERSION)
                val deviceModel = errorContent.getString(ReportField.PHONE_MODEL)
                val crashInfo =
                    "App Version Name: $appVersionName\nApp Version Code: $appVersionCode\nAndroid Version: $androidVersion\nDevice Model: $deviceModel\nStack Trace:\n $stackTrace"

                val file = File(context.filesDir, "last_crash.log").apply {
                    writeText(crashInfo)
                }

                // 2. Show notification
                showNotification(context, file.absolutePath)
            }
        }
    }

    private fun showNotification(context: Context, filePath: String) {
        val intent = CrashDetailsActivity.createIntent(context, filePath)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, context.getString(ly.com.tahaben.core.R.string.crash_reports_channel_id))
            .setContentTitle("App Crashed")
            .setContentText("Tap to view crash details")
            .setSmallIcon(ly.com.tahaben.core.R.drawable.farhan_transparent_bg)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        ContextCompat.getSystemService<NotificationManager>(
            context,
            NotificationManager::class.java
        )
            ?.notify(103, notification)
    }
}