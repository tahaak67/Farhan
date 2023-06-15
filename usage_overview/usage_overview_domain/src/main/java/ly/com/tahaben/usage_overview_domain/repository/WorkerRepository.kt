package ly.com.tahaben.usage_overview_domain.repository

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 01,May,2023
 */
interface WorkerRepository {
    fun scheduleWork()
    fun switchAutoCacheEnabled(isEnabled: Boolean)
    fun openAppSettings()
    fun switchReportsEnabled(usageReports: Map<String, Boolean>)
    fun scheduleWeeklyReport()
    fun scheduleMonthlyReport()
    fun scheduleYearlyReport()
}