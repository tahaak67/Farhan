package ly.com.tahaben.usage_overview_domain.repository

import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import java.time.LocalDate

interface UsageRepository {

    suspend fun getUsageEvents(date: LocalDate)
    suspend fun returnUsageEvents(date: LocalDate): List<UsageDataItem>
    suspend fun isDayDataFullyUpdated(date: LocalDate): Boolean
    fun checkUsagePermission(): Boolean
    suspend fun getCachedDays(): List<LocalDate>
    suspend fun deleteCacheForDay(date: LocalDate)

}