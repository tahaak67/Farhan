package ly.com.tahaben.notification_filter_domain.use_cases

import ly.com.tahaben.notification_filter_domain.model.FilterSchedule
import ly.com.tahaben.notification_filter_domain.preferences.Preferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

class IsCurrentTimeWithinFilterScheduleTest {

    // 2026-07-04 is a Saturday
    private val saturday = LocalDateTime.of(2026, 7, 4, 0, 0)
    private val sunday = saturday.plusDays(1)
    private val monday = saturday.plusDays(2)

    private fun useCase(schedule: FilterSchedule) =
        IsCurrentTimeWithinFilterSchedule(FakeSchedulePreferences(schedule))

    private fun daytimeWeekendSchedule(isEnabled: Boolean = true) = FilterSchedule(
        isEnabled = isEnabled,
        days = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
        startTime = LocalTime.of(8, 0),
        endTime = LocalTime.of(17, 0)
    )

    private fun overnightSaturdaySchedule() = FilterSchedule(
        isEnabled = true,
        days = setOf(DayOfWeek.SATURDAY),
        startTime = LocalTime.of(22, 0),
        endTime = LocalTime.of(7, 0)
    )

    @Test
    fun `filter is always active when schedule is disabled`() {
        val isWithinSchedule = useCase(daytimeWeekendSchedule(isEnabled = false))
        assertTrue(isWithinSchedule(monday.withHour(3)))
    }

    @Test
    fun `active within window on a selected day`() {
        val isWithinSchedule = useCase(daytimeWeekendSchedule())
        assertTrue(isWithinSchedule(saturday.withHour(12)))
        assertTrue(isWithinSchedule(saturday.withHour(8)))
    }

    @Test
    fun `inactive outside window on a selected day`() {
        val isWithinSchedule = useCase(daytimeWeekendSchedule())
        assertFalse(isWithinSchedule(saturday.withHour(7).withMinute(59)))
        assertFalse(isWithinSchedule(saturday.withHour(17)))
    }

    @Test
    fun `inactive on an unselected day even within window`() {
        val isWithinSchedule = useCase(daytimeWeekendSchedule())
        assertFalse(isWithinSchedule(monday.withHour(12)))
    }

    @Test
    fun `equal start and end times keep filter active all day on selected days`() {
        val isWithinSchedule = useCase(
            daytimeWeekendSchedule().copy(
                startTime = LocalTime.MIDNIGHT,
                endTime = LocalTime.MIDNIGHT
            )
        )
        assertTrue(isWithinSchedule(saturday.withHour(0)))
        assertTrue(isWithinSchedule(sunday.withHour(23).withMinute(59)))
        assertFalse(isWithinSchedule(monday.withHour(12)))
    }

    @Test
    fun `overnight window is active before midnight on the selected day`() {
        val isWithinSchedule = useCase(overnightSaturdaySchedule())
        assertTrue(isWithinSchedule(saturday.withHour(23)))
    }

    @Test
    fun `overnight window stays active past midnight into the next day`() {
        val isWithinSchedule = useCase(overnightSaturdaySchedule())
        assertTrue(isWithinSchedule(sunday.withHour(3)))
        assertFalse(isWithinSchedule(sunday.withHour(7)))
    }

    @Test
    fun `overnight window is inactive outside its hours`() {
        val isWithinSchedule = useCase(overnightSaturdaySchedule())
        assertFalse(isWithinSchedule(saturday.withHour(12)))
        assertFalse(isWithinSchedule(saturday.withHour(21).withMinute(59)))
    }

    @Test
    fun `overnight window past midnight is inactive when the previous day is not selected`() {
        val isWithinSchedule = useCase(
            overnightSaturdaySchedule().copy(days = setOf(DayOfWeek.SUNDAY))
        )
        assertFalse(isWithinSchedule(sunday.withHour(3)))
        assertTrue(isWithinSchedule(sunday.withHour(23)))
    }

    @Test
    fun `filter is never active when no days are selected`() {
        val isWithinSchedule = useCase(daytimeWeekendSchedule().copy(days = emptySet()))
        assertFalse(isWithinSchedule(saturday.withHour(12)))
    }
}

private class FakeSchedulePreferences(
    private val schedule: FilterSchedule
) : Preferences {
    override fun getFilterSchedule(): FilterSchedule = schedule
    override fun loadShouldShowOnBoarding(): Boolean = false
    override fun saveShouldShowOnBoarding(shouldShow: Boolean) = Unit
    override fun loadShouldShowcase(): Boolean = false
    override fun saveShouldShowcase(shouldShow: Boolean) = Unit
    override suspend fun isServiceEnabled(): Boolean = true
    override fun setServiceState(isEnabled: Boolean) = Unit
    override fun savePackageToNotificationExceptions(packageName: String) = Unit
    override fun removePackageFromNotificationExceptions(packageName: String) = Unit
    override fun isPackageInNotificationExceptions(packageName: String): Boolean = false
    override fun getNotificationFilterExceptionsList(): Set<String> = emptySet()
    override fun setNotifyMeTime(hour: Int, minutes: Int) = Unit
    override fun isNotifyMeScheduledToday(): Boolean = false
    override fun setNotifyMeScheduledDate(date: Long) = Unit
    override fun getNotifyMeHours(): Int = -1
    override fun getNotifyMeMinutes(): Int = -1
    override fun getSettingsShouldShowWarning(): Boolean = false
    override fun setSettingsShouldShowWarning(shouldShow: Boolean) = Unit
    override fun setFilterScheduleEnabled(isEnabled: Boolean) = Unit
    override fun setFilterScheduleDays(days: Set<DayOfWeek>) = Unit
    override fun setFilterScheduleStartTime(time: LocalTime) = Unit
    override fun setFilterScheduleEndTime(time: LocalTime) = Unit
}
