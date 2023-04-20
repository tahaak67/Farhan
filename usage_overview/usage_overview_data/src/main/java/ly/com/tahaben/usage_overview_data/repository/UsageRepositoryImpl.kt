package ly.com.tahaben.usage_overview_data.repository


import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.com.tahaben.usage_overview_data.local.UsageDao
import ly.com.tahaben.usage_overview_data.local.entity.DayLastUpdatedEntity
import ly.com.tahaben.usage_overview_data.mapper.toUsageDataItem
import ly.com.tahaben.usage_overview_data.mapper.toUsageDataItemEntity
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*


class UsageRepositoryImpl(
    private val context: Context,
    private val usageDao: UsageDao,
) : UsageRepository {

    override suspend fun getUsageEvents(date: LocalDate) {
        val usageDataItems = arrayListOf<UsageDataItem>()
        if (checkUsagePermission()) {
            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            val d = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
            val calendar = Calendar.getInstance()
            calendar.time = d
            val from = calendar.timeInMillis
            val fromD = calendar.time
            calendar.add(Calendar.DATE, 1)
            val to = calendar.timeInMillis
            val toD = calendar.time

            Timber.d("from= $from to = $to")
            Timber.d("from date= $fromD to date = $toD")
            withContext(Dispatchers.IO) {
                val usageEvents = usageStatsManager.queryEvents(from, to)
                val usageEvent = UsageEvents.Event()
                val pm: PackageManager = context.packageManager

                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(usageEvent)
                    val ai: ApplicationInfo? = try {
                        pm.getApplicationInfo(usageEvent.packageName, 0)
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                        null
                    }
                    val applicationName =
                        (if (ai != null) pm.getApplicationLabel(ai) else usageEvent.packageName) as String

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val applicationCategory =
                            (ai?.category ?: -1)
                        usageDataItems.add(
                            usageEvent.toUsageDataItem(
                                applicationName,
                                applicationCategory
                            )
                        )
                    } else {
                        usageDataItems.add(usageEvent.toUsageDataItem(applicationName))
                    }
                    if (usageEvent.eventType == 1 || usageEvent.eventType == 2)
                        Timber.e("APP ${usageEvent.packageName} ${usageEvent.timeStamp}")
                }
                usageDataItems.forEach { item ->
                    usageDao.insertUsageItem(
                        item.toUsageDataItemEntity()
                    )
                }
                if (usageDataItems.isNotEmpty()) {
                    usageDao.setLastDbUpdateTimeForDay(
                        DayLastUpdatedEntity(date, System.currentTimeMillis())
                    )
                }
            }
        } else {
            // Navigate the user to the permission settings
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                this.addFlags(FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this)
            }
        }
    }

    override fun checkUsagePermission(): Boolean {
        val appOpsManager =
            context.getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
        // `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(), "ly.com.tahaben.farhan"
            )
        } else {
            appOpsManager.checkOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(), "ly.com.tahaben.farhan"
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override suspend fun returnUsageEvents(date: LocalDate): List<UsageDataItem> {
        val d = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val calendar = Calendar.getInstance()
        calendar.time = d
        val from = calendar.timeInMillis
        calendar.add(Calendar.DATE, 1)
        val to = calendar.timeInMillis
        Timber.d("from: ${from} to: $to")
        val results = usageDao.getUsageItemsForRange(from, to).map { item ->
            item.toUsageDataItem()
        }
        Timber.d("result size: ${results.size}")
        return results
    }


    override suspend fun isDayDataFullyUpdated(date: LocalDate): Boolean {
        val lastUpdateTimeForDay = usageDao.getLastDbUpdateTimeForDay(date)
        if (lastUpdateTimeForDay == null) {
            Timber.d("data never updated")
            return false
        } else {
            val endOfDay =
                date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
            val timeBetweenUpdateAndEndOfDay = lastUpdateTimeForDay - endOfDay
            Timber.d("data updated at $lastUpdateTimeForDay")
            Timber.d("time Between Update And EndOfDay $timeBetweenUpdateAndEndOfDay")

            return timeBetweenUpdateAndEndOfDay >= 0
        }
    }

    override suspend fun getCachedDays(): List<LocalDate> {
        return usageDao.getFullyUpdatedDays()
    }

    override suspend fun deleteCacheForDay(date: LocalDate) {
        usageDao.deleteInfoForDay(date)
        usageDao.deleteLastDbUpdateTimeForDay(date)
    }
}