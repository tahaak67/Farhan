package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import java.time.LocalDate

class GetUsageDataForDate(private val repository: UsageRepository) {
    suspend operator fun invoke(date: LocalDate): List<UsageDataItem> {
        return repository.getUsageEvents(date)
    }
}
