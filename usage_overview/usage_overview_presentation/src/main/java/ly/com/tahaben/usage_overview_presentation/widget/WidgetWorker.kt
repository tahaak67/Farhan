package ly.com.tahaben.usage_overview_presentation.widget

import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.preferences.Preferences
import ly.com.tahaben.usage_overview_domain.use_case.UsageOverviewUseCases
import timber.log.Timber
import java.time.LocalDate

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 12,Jun,2024
 */

class WidgetWorker constructor(
    private val context: Context,
    params: WorkerParameters,
    private val useCases: UsageOverviewUseCases,
    private val preferences: Preferences
) : CoroutineWorker(context, params) {
    companion object{
        const val UPDATE_WIDGET_NOTIFICATION_ID =   104
    }
    private var filterFarhan = false
    private var filterLaunchers = false


    override suspend fun doWork(): Result {
        Timber.d("Widget worker here")
        setForeground(createForegroundInfo(context.getString(ly.com.tahaben.core.R.string.widget_update_notification)))
        filterLaunchers = preferences.isIgnoreLauncher()
        filterFarhan = preferences.isIgnoreFarhan()
        val todayUsage = getUsageDurationMillis()
        val yesterdayUsage = getUsageDurationYesterday()
        preferences.setTodayUsage(todayUsage)
        preferences.setYesterdayUsage(yesterdayUsage)

        UsageWidget.apply {
            updateAll(context)
        }
        return Result.success()
    }
    private fun createForegroundInfo(text: String): ForegroundInfo {
        val id = applicationContext.getString(ly.com.tahaben.core.R.string.fetch_usage_info_id)
        val title = applicationContext.getString(ly.com.tahaben.core.R.string.cache_worker_notification_title)


        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setContentText(text)
            .setTicker(title)
            .setSmallIcon(ly.com.tahaben.core.R.drawable.farhan_transparent_bg)
            .setSilent(true)
            .setOngoing(true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(UPDATE_WIDGET_NOTIFICATION_ID,
                notification,
                FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(UPDATE_WIDGET_NOTIFICATION_ID,
                notification)
        }
    }

    suspend fun getUsageDurationMillis(): Long {
        Timber.d("get usecase")
        val today = LocalDate.now()
        val usageData = useCases.getUsageDataForDate(today)
             .filterNot { it.appCategory == UsageDataItem.Category.LAUNCHER }
        Timber.d("get usage data")
        val filteredData = useCases.filterUsageEvents(usageData).dropLast(1).filterNot {
            (filterLaunchers && it.appCategory == UsageDataItem.Category.LAUNCHER) ||
                    (filterFarhan && it.packageName == "ly.com.tahaben.farhan")
        }
        Timber.d("filter usage data  ...")
        filteredData.forEachIndexed { index, usageDataItem ->
            Timber.d("$index: ${usageDataItem.appName}:${usageDataItem.usageTimestamp}")
        }
        val usage = useCases.calculateUsageDuration(
            filteredData,
            useCases.getDurationFromMilliseconds,
            useCases.filterDuration
        )
        Timber.d("Usage list $usage")
        return usage.sumOf { it.usageDurationInMilliseconds }
    }
    suspend fun getUsageDurationYesterday(): Long {

        Timber.d("get usecase")
        val yesterday = LocalDate.now().minusDays(1)
        val usageData = useCases.getUsageEventsFromDb(yesterday)
            .filterNot { it.appCategory == UsageDataItem.Category.LAUNCHER }
        Timber.d("get usage data")
        val filteredData = useCases.filterUsageEvents(usageData).filterNot {
            (filterLaunchers && it.appCategory == UsageDataItem.Category.LAUNCHER) ||
                    (filterFarhan && it.packageName == "ly.com.tahaben.farhan")
        }
        Timber.d("filter usage data  ...")
        filteredData.forEachIndexed { index, usageDataItem ->
            Timber.d("$index: ${usageDataItem.appName}:${usageDataItem.usageTimestamp}")
        }
        val usage = useCases.calculateUsageDuration(
            filteredData,
            useCases.getDurationFromMilliseconds,
            useCases.filterDuration
        )
        Timber.d("Usage list $usage")
        return usage.sumOf { it.usageDurationInMilliseconds }
    }
}