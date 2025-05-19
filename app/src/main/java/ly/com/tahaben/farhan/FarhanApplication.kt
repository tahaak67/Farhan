package ly.com.tahaben.farhan

//import androidx.hilt.work.HiltWorkerFactory
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import ly.com.tahaben.core.R
import org.acra.ReportField
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class FarhanApplication : Application() {

    @Inject
    lateinit var farhanWorkerConfiguration: Configuration

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onCreate() {
        super.onCreate()
        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            // Basic crash data collection
            reportContent = listOf(
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,
                ReportField.STACK_TRACE
            )


        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.fetch_usage_info_id),
                getString(R.string.fetch_usage_info),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val crashReportChannel = NotificationChannel(
                getString(R.string.crash_reports_channel_id),
                getString(R.string.crash_reports_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = getString(R.string.fetch_usage_info_description)
            crashReportChannel.description = getString(R.string.crash_reports_channel_description)
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(crashReportChannel)
        }
        WorkManager.initialize(this, farhanWorkerConfiguration)
    }

}