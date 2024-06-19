package ly.com.tahaben.usage_overview_domain.use_case

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class IsDayOverTest {

    private val isDateOver = IsDayOver()


        @Test
        fun `isDateOver with pastDate returns True`() {
            val pastDate = LocalDate.now().minusDays(1)
            assertTrue(isDateOver(pastDate))
        }

        @Test
        fun `isDateOver with currentDate returnsFalse`() {
            val currentDate = LocalDate.now()
            assertFalse(isDateOver(currentDate))
        }

        @Test
        fun `isDateOver with futureDate returnsFalse`() {
            val futureDate = LocalDate.now().plusDays(1)
            assertFalse(isDateOver(futureDate))
        }

}