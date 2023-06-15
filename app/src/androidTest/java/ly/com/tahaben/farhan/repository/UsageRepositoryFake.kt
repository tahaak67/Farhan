package ly.com.tahaben.farhan.repository

import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import java.time.LocalDate

class UsageRepositoryFake : UsageRepository {

    val usageItems = mutableListOf<UsageDataItem>()
    var permissionGranted = true

    override suspend fun cacheUsageEvents(date: LocalDate) {

    }

    override fun checkUsagePermission(): Boolean {
        return permissionGranted
    }

    override suspend fun getUsageEvents(date: LocalDate): List<UsageDataItem> {
        return usageItems
    }

    override suspend fun getUsageEventsFromDb(date: LocalDate): List<UsageDataItem> {
        return usageItems
    }

    override suspend fun isDayDataFullyUpdated(date: LocalDate): Boolean {
        return false
    }

    override suspend fun getCachedDays(): List<LocalDate> {
        return emptyList()
    }

    override suspend fun deleteCacheForDay(date: LocalDate) {

    }
}