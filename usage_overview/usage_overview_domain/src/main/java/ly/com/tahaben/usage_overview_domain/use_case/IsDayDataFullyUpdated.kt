package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import java.time.LocalDate

class IsDayDataFullyUpdated(private val usageRepository: UsageRepository) {

    suspend operator fun invoke(day: LocalDate): Boolean {
        return usageRepository.isDayDataFullyUpdated(day)
    }

}
