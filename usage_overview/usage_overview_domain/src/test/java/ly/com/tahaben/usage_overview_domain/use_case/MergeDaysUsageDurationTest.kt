package ly.com.tahaben.usage_overview_domain.use_case

import com.google.common.truth.Truth.assertThat
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.model.UsageDurationDataItem
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.minutes

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 26,May,2023
 */
class MergeDaysUsageDurationTest {

    private lateinit var mergeDaysUsageDuration: MergeDaysUsageDuration
    private lateinit var getDurationFromMilliseconds: GetDurationFromMilliseconds

    @Before
    fun setUp() {
        mergeDaysUsageDuration = MergeDaysUsageDuration()
        getDurationFromMilliseconds = GetDurationFromMilliseconds()
    }

    @Test
    fun `multiple app usage duration items merged into one`() {

        val usageDataItems = listOf(
            UsageDurationDataItem(
                "Farhan",
                "ly.com.tahaben.farhan",
                1.minutes.inWholeMilliseconds,
                UiText.MixedString(1, R.string.minutes),
                UiText.StringResource(R.string.category_productivity),
                UsageDataItem.Category.PRODUCTIVITY
            ),
            UsageDurationDataItem(
                "App2",
                "com.app2",
                2.minutes.inWholeMilliseconds,
                UiText.MixedString(2, R.string.minutes),
                UiText.StringResource(R.string.category_other),
                UsageDataItem.Category.OTHER
            ),
            UsageDurationDataItem(
                "Farhan",
                "ly.com.tahaben.farhan",
                3.minutes.inWholeMilliseconds,
                UiText.MixedString(3, R.string.minutes),
                UiText.StringResource(R.string.category_productivity),
                UsageDataItem.Category.PRODUCTIVITY
            ),
            UsageDurationDataItem(
                "App3",
                "com.app3",
                4.minutes.inWholeMilliseconds,
                UiText.MixedString(4, R.string.minutes),
                UiText.StringResource(R.string.category_social),
                UsageDataItem.Category.SOCIAL
            )
        )
        val expectedOutput = listOf(
            UsageDurationDataItem(
                "Farhan",
                "ly.com.tahaben.farhan",
                4.minutes.inWholeMilliseconds,
                UiText.MixedString(4, R.string.minutes),
                UiText.StringResource(R.string.category_productivity),
                UsageDataItem.Category.PRODUCTIVITY
            ),
            UsageDurationDataItem(
                "App2",
                "com.app2",
                2.minutes.inWholeMilliseconds,
                UiText.MixedString(2, R.string.minutes),
                UiText.StringResource(R.string.category_other),
                UsageDataItem.Category.OTHER
            ),
            UsageDurationDataItem(
                "App3",
                "com.app3",
                4.minutes.inWholeMilliseconds,
                UiText.MixedString(4, R.string.minutes),
                UiText.StringResource(R.string.category_social),
                UsageDataItem.Category.SOCIAL
            )
        )
        val actualOutput = mergeDaysUsageDuration(usageDataItems, getDurationFromMilliseconds)
        assertThat(actualOutput).isEqualTo(expectedOutput)
    }
}