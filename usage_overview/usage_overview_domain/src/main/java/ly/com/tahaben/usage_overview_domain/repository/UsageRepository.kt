package ly.com.tahaben.usage_overview_domain.repository

import kotlinx.coroutines.flow.Flow
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import java.time.LocalDate

interface UsageRepository {

    suspend fun getUsageEvents(date: LocalDate): Flow<List<UsageDataItem>>

    fun checkUsagePermission(): Boolean

}