package ly.com.tahaben.usage_overview_data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ly.com.tahaben.usage_overview_data.local.CacheWorker
import ly.com.tahaben.usage_overview_data.local.ReportsWorker
import ly.com.tahaben.usage_overview_domain.preferences.Preferences
import ly.com.tahaben.usage_overview_domain.repository.WorkerRepository
import ly.com.tahaben.usage_overview_domain.util.WorkerKeys
import timber.log.Timber
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 01,May,2023
 */
class WorkerRepoImpl(
    private val workManager: WorkManager,
    private val preferences: Preferences,
    private val context: Context
) : WorkerRepository {

    override fun scheduleWork() {
        val now = LocalTime.now()
        val currentDateTime = LocalDateTime.of(LocalDate.now(), now)
        val nextMidnight = currentDateTime.withHour(0).withMinute(0).withSecond(0).plusDays(1)
        val timeLeft = Duration.between(currentDateTime, nextMidnight).toMinutes() + 1
        val cacheRequest = PeriodicWorkRequestBuilder<CacheWorker>(Duration.ofHours(12))
            .setInitialDelay(Duration.ofMinutes(timeLeft))
            .build()
        workManager.enqueueUniquePeriodicWork(
            WorkerKeys.DAILY_CACHE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            cacheRequest
        )
        Timber.d("enqueued work time left: $timeLeft")
    }

    override fun switchAutoCacheEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            scheduleWork()
            Timber.d(
                "work queue: ${
                    workManager.getWorkInfosForUniqueWork(WorkerKeys.DAILY_CACHE_WORK_NAME).get()
                }"
            )
        } else {
            workManager.cancelUniqueWork(WorkerKeys.DAILY_CACHE_WORK_NAME)
            Timber.d(
                "work queue: ${
                    workManager.getWorkInfosForUniqueWork(WorkerKeys.DAILY_CACHE_WORK_NAME).get()
                }"
            )
        }
        preferences.setAutoCacheEnabled(isEnabled)
    }

    override fun openAppSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }

    override fun switchReportsEnabled(usageReports: Map<String, Boolean>) {
        if (usageReports[WorkerKeys.WEEKLY_USAGE_REPORTS] == true) {
            scheduleWeeklyReport()
            Timber.d(
                "work queue: ${
                    workManager.getWorkInfosForUniqueWork(WorkerKeys.WEEKLY_USAGE_REPORTS).get()
                }"
            )
        } else {
            workManager.cancelUniqueWork(WorkerKeys.WEEKLY_USAGE_REPORTS)
            Timber.d(
                "work queue: ${
                    workManager.getWorkInfosForUniqueWork(WorkerKeys.WEEKLY_USAGE_REPORTS).get()
                }"
            )
        }
        if (usageReports[WorkerKeys.MONTHLY_USAGE_REPORTS] == true) {
            scheduleMonthlyReport()
            Timber.d(
                "work queue: ${
                    workManager.getWorkInfosForUniqueWork(WorkerKeys.MONTHLY_USAGE_REPORTS).get()
                }"
            )
        } else {
            workManager.cancelUniqueWork(WorkerKeys.MONTHLY_USAGE_REPORTS)
            Timber.d(
                "work queue: ${
                    workManager.getWorkInfosForUniqueWork(WorkerKeys.MONTHLY_USAGE_REPORTS).get()
                }"
            )
        }
        preferences.setUsageReportsEnabled(usageReports)
    }

    override fun scheduleWeeklyReport() {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val now = LocalTime.now()
        val today = LocalDate.now()
        val currentDateTime = LocalDateTime.of(LocalDate.now(), now)
        val firstDayOfWeekDateTime =
            today.with(TemporalAdjusters.next(firstDayOfWeek)).atStartOfDay()
        val timeLeft = Duration.between(currentDateTime, firstDayOfWeekDateTime).toMinutes() + 1
        val inputData = Data.Builder()
            .putString(WorkerKeys.REPORT_TYPE, WorkerKeys.WEEKLY_USAGE_REPORTS)
            .build()
        val cacheRequest = PeriodicWorkRequestBuilder<ReportsWorker>(Duration.ofDays(7))
            .setInitialDelay(Duration.ofMinutes(timeLeft))
            .setInputData(inputData)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WorkerKeys.WEEKLY_USAGE_REPORTS,
            ExistingPeriodicWorkPolicy.KEEP,
            cacheRequest
        )
        Timber.d("enqueued work weekly report time left: $timeLeft")
    }

    override fun scheduleMonthlyReport() {
        val now = LocalTime.now()
        val today = LocalDate.now()
        val currentDateTime = LocalDateTime.of(LocalDate.now(), now)
        val firstDayOfMonthDateTime =
            today.with(TemporalAdjusters.firstDayOfNextMonth()).atStartOfDay()
        val timeLeft = Duration.between(currentDateTime, firstDayOfMonthDateTime).toMinutes() + 1
        val inputData = Data.Builder()
            .putString(WorkerKeys.REPORT_TYPE, WorkerKeys.MONTHLY_USAGE_REPORTS)
            .build()
        val cacheRequest = PeriodicWorkRequestBuilder<ReportsWorker>(Duration.ofDays(30))
            .setInitialDelay(Duration.ofMinutes(timeLeft))
            .setInputData(inputData)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WorkerKeys.MONTHLY_USAGE_REPORTS,
            ExistingPeriodicWorkPolicy.KEEP,
            cacheRequest
        )
        Timber.d("enqueued work monthly report time left: $timeLeft")
    }

    override fun scheduleYearlyReport() {
        //TODO("Not yet implemented")
    }
}