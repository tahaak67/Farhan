package ly.com.tahaben.usage_overview_data.local

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ly.com.tahaben.core.R
import ly.com.tahaben.core.navigation.Routes
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.model.UsageDurationDataItem
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import ly.com.tahaben.usage_overview_domain.use_case.UsageOverviewUseCases
import ly.com.tahaben.usage_overview_domain.util.WorkerKeys
import timber.log.Timber
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 29,Apr,2023
 */

class ReportsWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val usageRepository: UsageRepository,
    private val usageOverviewUseCases: UsageOverviewUseCases
) : CoroutineWorker(appContext = context, params = workerParams) {

    companion object {
        const val USAGE_REPORT_NOTIFICATION_ID = 103
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    override suspend fun doWork(): Result {
        Timber.d("doing work")
        val reportType = inputData.getString(WorkerKeys.REPORT_TYPE)
        return try {
            when (reportType) {
                WorkerKeys.WEEKLY_USAGE_REPORTS -> {
                    calculateAndPostWeeklyReport()
                    Result.success()
                }

                WorkerKeys.MONTHLY_USAGE_REPORTS -> {
                    calculateAndPostMonthlyReport()
                    Result.success()
                }

                else -> Result.failure()
            }

        } catch (ex: Exception) {
            Timber.e(ex, "An error occurred during work execution")
            Result.failure()
        }
    }

    private suspend fun calculateAndPostWeeklyReport() {
        val today = LocalDate.now()
        // get the end of the week for the device locale
        val endOfWeekDay = WeekFields.of(Locale.getDefault()).firstDayOfWeek.plus(6)
        Timber.d("end of week ${endOfWeekDay.name}")
        val endOfLastWeek = today.with(TemporalAdjusters.previous(endOfWeekDay))
        Timber.d("endOfLastWeek: $endOfLastWeek")
        val startOfLastWeek = endOfLastWeek.minusDays(6)
        Timber.d("startOfLastWeek: $startOfLastWeek")
        val endOfTheWeekBefore = startOfLastWeek.minusDays(1)
        Timber.d("endOfLastWeekBefore: $endOfTheWeekBefore")
        val startOfTheWeekBefore = endOfTheWeekBefore.minusDays(6)
        Timber.d("startOfTheWeekBefore: $startOfTheWeekBefore")

        val totalTimeForLastWeek = refreshUsageDataForRange(startOfLastWeek, endOfLastWeek)
        val totalTimeForTheWeekBefore =
            refreshUsageDataForRange(startOfTheWeekBefore, endOfTheWeekBefore)
        val difference = totalTimeForLastWeek - totalTimeForTheWeekBefore
        Timber.d("time for last week: $totalTimeForLastWeek")
        Timber.d("time for the week before: $totalTimeForTheWeekBefore")

        val message = difference.milliseconds.toComponents { hours, minutes, _, _ ->
            when {
                difference > 0 -> {
                    if (hours == 0L) {
                        context.getString(R.string.weekly_usage_report_msg_more_min, minutes)
                    } else if (minutes == 0) {
                        context.getString(R.string.weekly_usage_report_msg_more_hrs, hours)
                    } else {
                        context.getString(
                            R.string.weekly_usage_report_msg_more_hrs_min,
                            hours,
                            minutes
                        )
                    }
                }

                difference < 0 -> {
                    if (hours == 0L) {
                        context.getString(R.string.weekly_usage_report_msg_less_min, minutes)
                    } else if (minutes == 0) {
                        context.getString(R.string.weekly_usage_report_msg_less_hrs, hours)
                    } else {
                        context.getString(
                            R.string.weekly_usage_report_msg_less_hrs_min,
                            hours,
                            minutes
                        )
                    }
                }

                else -> {
                    context.getString(R.string.weekly_usage_report_same_time)
                }
            }
        }

        showNotification(
            title = context.getString(R.string.weekly_report_notification_title),
            message = message,
            startDate = startOfLastWeek.toString(),
            endDate = endOfLastWeek.toString()
        )
        Timber.d("show notification")
    }

    private suspend fun calculateAndPostMonthlyReport() {
        val today = LocalDate.now()
        val endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())
        Timber.d("endOfMonth: $endOfMonth")
        val startOfMonth = endOfMonth.withDayOfMonth(1)
        Timber.d("startOfMonth: $startOfMonth")

        val endOfLastMonth = startOfMonth.minusDays(1)
        Timber.d("endOfLastMonth: $endOfLastMonth")
        val startOfLastMonth = endOfLastMonth.withDayOfMonth(1)
        Timber.d("startOfLastMonth: $startOfLastMonth")

        val endOfMonthBefore = startOfLastMonth.minusDays(1)
        Timber.d("endOfMonthBefore: $endOfMonthBefore")
        val startOfMonthBefore = endOfMonthBefore.withDayOfMonth(1)
        Timber.d("startOfMonthBefore: $startOfMonthBefore")

        val totalTimeForLastMonth = refreshUsageDataForRange(startOfLastMonth, endOfLastMonth)
        val totalTimeForMonthBefore = refreshUsageDataForRange(startOfMonthBefore, endOfMonthBefore)
        val difference = totalTimeForLastMonth - totalTimeForMonthBefore
        Timber.d("time for last month: $totalTimeForLastMonth")
        Timber.d("time for the month before: $totalTimeForMonthBefore")

        val message = difference.milliseconds.toComponents { hours, minutes, _, _ ->
            when {
                difference > 0 -> {
                    if (hours == 0L) {
                        context.getString(R.string.monthly_usage_report_msg_more_min, minutes)
                    } else if (minutes == 0) {
                        context.getString(R.string.monthly_usage_report_msg_more_hrs, hours)
                    } else {
                        context.getString(
                            R.string.monthly_usage_report_msg_more_hrs_min,
                            hours,
                            minutes
                        )
                    }
                }

                difference < 0 -> {
                    if (hours == 0L) {
                        context.getString(R.string.monthly_usage_report_msg_less_min, minutes)
                    } else if (minutes == 0) {
                        context.getString(R.string.monthly_usage_report_msg_less_hrs, hours)
                    } else {
                        context.getString(
                            R.string.monthly_usage_report_msg_less_hrs_min,
                            hours,
                            minutes
                        )
                    }
                    context.getString(
                        R.string.monthly_usage_report_msg_less_hrs_min,
                        hours,
                        minutes
                    )

                }

                else -> {
                    context.getString(R.string.monthly_usage_report_same_time)
                }
            }
        }

        showNotification(
            title = context.getString(R.string.monthly_report_notification_title),
            message = message,
            startDate = startOfLastMonth.toString(),
            endDate = endOfLastMonth.toString()
        )
        Timber.d("show notification")
    }


    private fun showNotification(
        title: String,
        message: String,
        startDate: String,
        endDate: String
    ) {
        Timber.d("start end date: $startDate : $endDate")
        val deepLinkUri =
            Uri.parse("app://${context.packageName}/${Routes.USAGE}/$startDate/$endDate")
        val pm = context.packageManager
        val showUsageIntent = pm.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            action = Intent.ACTION_VIEW
            data = deepLinkUri
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            showUsageIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification =
            NotificationCompat.Builder(context, context.getString(R.string.fetch_usage_info_id))
                .setStyle(NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.ic_farhan_transparent)
                .setSilent(false)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(USAGE_REPORT_NOTIFICATION_ID, notification)
        }
    }

    private suspend fun refreshUsageDataForRange(from: LocalDate, to: LocalDate): Long {
        var totalTimeInMilliSeconds = 0L

        var date = from
        val filteredListForRange = mutableListOf<UsageDurationDataItem>()
        while (date <= to) {
            val usageDataList = usageOverviewUseCases.getUsageEventsFromDb(date)

            val filteredList =
                usageOverviewUseCases.filterUsageEvents(usageDataList)
            filteredList.map {
                it.usageTimestamp
            }
            val filteredListWithDuration =
                usageOverviewUseCases.calculateUsageDuration(
                    filteredList,
                    usageOverviewUseCases.getDurationFromMilliseconds,
                    usageOverviewUseCases.filterDuration
                ).sortedByDescending { it.usageDurationInMilliseconds }
            filteredListForRange.addAll(filteredListWithDuration)
            totalTimeInMilliSeconds += filteredListWithDuration.sumOf { it.usageDurationInMilliseconds }
            val totalSocialUsageMilli = filteredListWithDuration
                .filter { it.appCategory == UsageDataItem.Category.SOCIAL }
                .sumOf { it.usageDurationInMilliseconds }
            val totalProductivityUsageMilli = filteredListWithDuration
                .filter { it.appCategory == UsageDataItem.Category.PRODUCTIVITY }
                .sumOf { it.usageDurationInMilliseconds }
            val totalGameUsageMilli = filteredListWithDuration
                .filter { it.appCategory == UsageDataItem.Category.GAME }
                .sumOf { it.usageDurationInMilliseconds }
            date = date.plusDays(1)
        }
        return totalTimeInMilliSeconds
    }

}