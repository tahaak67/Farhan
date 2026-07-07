package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.preferences.Preferences
import java.time.LocalDateTime

class IsCurrentTimeWithinFilterSchedule(
    private val sharedPref: Preferences
) {

    operator fun invoke(now: LocalDateTime = LocalDateTime.now()): Boolean {
        val schedule = sharedPref.getFilterSchedule()
        if (!schedule.isEnabled) {
            return true
        }
        val day = now.dayOfWeek
        val time = now.toLocalTime()
        return when {
            // equal start and end means the filter covers the whole day
            schedule.startTime == schedule.endTime -> day in schedule.days

            schedule.startTime.isBefore(schedule.endTime) ->
                day in schedule.days &&
                        !time.isBefore(schedule.startTime) &&
                        time.isBefore(schedule.endTime)

            // end before start means the window crosses midnight and belongs
            // to the day it starts on, e.g. Saturday 22:00 - 07:00 covers
            // Sunday 03:00 when Saturday is selected
            else ->
                (day in schedule.days && !time.isBefore(schedule.startTime)) ||
                        (day.minus(1) in schedule.days && time.isBefore(schedule.endTime))
        }
    }
}
