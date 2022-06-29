package ly.com.tahaben.usage_overview_domain.use_case

import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class IsDateToday {

    operator fun invoke(localDate: LocalDate): Boolean {
        val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        return date == today
    }
}