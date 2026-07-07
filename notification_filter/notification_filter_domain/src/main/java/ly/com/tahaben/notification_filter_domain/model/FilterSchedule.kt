package ly.com.tahaben.notification_filter_domain.model

import java.time.DayOfWeek
import java.time.LocalTime

data class FilterSchedule(
    val isEnabled: Boolean,
    val days: Set<DayOfWeek>,
    val startTime: LocalTime,
    val endTime: LocalTime
)
