package ly.com.tahaben.usage_overview_domain.use_case

import java.time.LocalDate

class IsDayOver {
    operator fun invoke(date: LocalDate): Boolean {
        val currentDate = LocalDate.now()
        return date.isBefore(currentDate)
    }
}
