package ly.com.tahaben.usage_overview_domain.use_case

import kotlinx.coroutines.flow.Flow
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import java.time.LocalDate

class GetUsageDataForDate(
    private val repository: UsageRepository
) {

    suspend operator fun invoke(date: LocalDate): Flow<List<UsageDataItem>> {
        return repository.getUsageEvents(date)
    }

}