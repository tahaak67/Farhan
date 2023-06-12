package ly.com.tahaben.usage_overview_data.local

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ly.com.tahaben.core.R
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 29,Apr,2023
 */

class CacheWorker(
    private val context: Context,
    private val workerParams: WorkerParameters,
    private val usageRepository: UsageRepository
) : CoroutineWorker(appContext = context, params = workerParams) {

    companion object {
        const val FETCH_USAGE_NOTIFICATION_ID = 102
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    override suspend fun doWork(): Result {
        Timber.d("doing work")
        return try {
            val yesterday = LocalDate.now().minusDays(1)
            if (usageRepository.isDayDataFullyUpdated(yesterday)) {
                return Result.success()
            }
            showNotification(yesterday)
            Timber.d("show notification")
            usageRepository.cacheUsageEvents(yesterday)
            notificationManager.cancel(FETCH_USAGE_NOTIFICATION_ID)
            Result.success()
        } catch (ex: Exception) {
            ex.printStackTrace()
            notificationManager.cancel(FETCH_USAGE_NOTIFICATION_ID)
            Result.failure()
        }
    }

    private fun showNotification(date: LocalDate) {
        val notification =
            NotificationCompat.Builder(context, context.getString(R.string.fetch_usage_info_id))
                .setStyle(NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.ic_farhan_transparent)
                .setSilent(false)
                .setContentTitle(context.getString(R.string.cache_worker_notification_title))
                .setContentText(
                    context.getString(
                        R.string.cache_worker_notification_text,
                        parseDate(date)
                    )
                )
                .setProgress(0, 0, true)
                .setOngoing(true)
                .build()


        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(FETCH_USAGE_NOTIFICATION_ID, notification)
        }

    }

    private fun parseDate(date: LocalDate): String {
        return DateTimeFormatter.ofPattern("dd LLLL").format(date)
    }
}