package ly.com.tahaben.farhan.repository

import ly.com.tahaben.usage_overview_domain.repository.WorkerRepository

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 12,Jun,2023
 */
class WorkerRepositoryFake : WorkerRepository {
    override fun scheduleWork() {
        TODO("Not yet implemented")
    }

    override fun switchAutoCacheEnabled(isEnabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun openAppSettings() {
        TODO("Not yet implemented")
    }

    override fun switchReportsEnabled(usageReports: Map<String, Boolean>) {
        TODO("Not yet implemented")
    }

    override fun scheduleWeeklyReport() {
        TODO("Not yet implemented")
    }

    override fun scheduleMonthlyReport() {
        TODO("Not yet implemented")
    }

    override fun scheduleYearlyReport() {
        TODO("Not yet implemented")
    }

    override fun checkIfBackgroundWorkRestricted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun requestToIgnoreBatteryOptimization() {
        TODO("Not yet implemented")
    }
}