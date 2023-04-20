package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import java.time.LocalDate

class ReturnUsageEvents(private val usageRepository: UsageRepository) {

    suspend operator fun invoke(date: LocalDate): List<UsageDataItem> {
        return usageRepository.returnUsageEvents(date)
    }

}
