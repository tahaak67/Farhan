package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import java.time.LocalDate

class CacheUsageDataForDate(
    private val repository: UsageRepository
) {

    suspend operator fun invoke(date: LocalDate) {
        repository.cacheUsageEvents(date)
    }

}