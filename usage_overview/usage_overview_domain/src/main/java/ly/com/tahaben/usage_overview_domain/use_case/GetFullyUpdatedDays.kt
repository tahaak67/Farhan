package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import java.time.LocalDate

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 10,Apr,2023
 */
class GetFullyUpdatedDays(private val repo: UsageRepository) {
    suspend operator fun invoke(): List<LocalDate> {
        return repo.getCachedDays()
    }
}