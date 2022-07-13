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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import ly.com.tahaben.usage_overview_data.mapper.toUsageDataItem
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class UsageRepositoryImpl(
    private val context: Context
) : UsageRepository {

    override suspend fun getUsageEvents(date: LocalDate): Flow<List<UsageDataItem>> {
        val usageDataItems = arrayListOf<UsageDataItem>()
        if (checkUsagePermission()) {
            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager // Context.USAGE_STATS_SERVICE);

            val cly = Calendar.getInstance()
            val d = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
            val calendar = Calendar.getInstance()
            calendar.time = d
            cly.set(Calendar.HOUR_OF_DAY, 0)
            cly.set(Calendar.MINUTE, 0)
            cly.set(Calendar.SECOND, 0)
            val from = calendar.timeInMillis
            val fromD = calendar.time
            calendar.add(Calendar.DATE, 1)
            val to = calendar.timeInMillis
            val toD = calendar.time

            Timber.d("from= ${from} to = $to")
            Timber.d("from date= ${fromD} to date = ${toD}")
            withContext(Dispatchers.IO) {
                val usageEvents = usageStatsManager.queryEvents(from, to)
                val usageEvent = UsageEvents.Event()
                val pm: PackageManager =
                    context.packageManager

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
                    Timber.e(
                        "APP" +
                                "${usageEvent.packageName} ${usageEvent.timeStamp} "
                    )
                }
            }
            return flowOf(usageDataItems)
        } else {
            // Navigate the user to the permission settings
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                this.addFlags(FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this)
            }
            return flowOf(usageDataItems)
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
}