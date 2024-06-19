package ly.com.tahaben.farhan

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
//import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import ly.com.tahaben.core.R
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.fetch_usage_info_id),
                getString(R.string.fetch_usage_info),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = getString(R.string.fetch_usage_info_description)
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        WorkManager.initialize(this, farhanWorkerConfiguration)
    }

}