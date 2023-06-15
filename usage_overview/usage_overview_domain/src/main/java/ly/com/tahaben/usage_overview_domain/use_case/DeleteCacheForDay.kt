package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import java.time.LocalDate

class DeleteCacheForDay(private val repo: UsageRepository) {
    suspend operator fun invoke(day: LocalDate) {
        repo.deleteCacheForDay(day)
    }
}
