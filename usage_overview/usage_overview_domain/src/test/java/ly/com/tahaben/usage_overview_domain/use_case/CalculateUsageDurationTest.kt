package ly.com.tahaben.usage_overview_domain.use_case

import com.google.common.truth.Truth.assertThat
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.model.UsageDurationDataItem
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class CalculateUsageDurationTest {

    private lateinit var calculateUsageDuration: CalculateUsageDuration
    private lateinit var getDurationFromMilliseconds: GetDurationFromMilliseconds
    private lateinit var filterDuration: FilterDuration

    @Before
    fun setUp() {
        calculateUsageDuration = CalculateUsageDuration()
        getDurationFromMilliseconds = GetDurationFromMilliseconds()
        filterDuration = FilterDuration()
    }

    @Test
    fun `Total usage duration properly calculated`() {
        val apps = listOf("myApp", "another app", "some app i forgot to uninstall", "bruh")
        val packageName = listOf(
            "com.ex.myApp",
            "hi.another app",
            "cu.some app i forgot to uninstall",
            "cri.bruh"
        )
        val usageItems = (1..33).map {
            val rnd = Random.nextInt(0, 3)
            UsageDataItem(
                appName = apps[rnd],
                packageName = packageName[rnd],
                usageTimestamp = Random.nextLong(),
                usageType = UsageDataItem.EventType.values().random(),
                appCategory = UsageDataItem.Category.values().random(),
            )
        }
        val result = calculateUsageDuration(usageItems, getDurationFromMilliseconds, filterDuration)
            .sortedBy { it.usageDurationInMilliseconds }
        val expectedUsageDurationList = ArrayList<UsageDurationDataItem>()
        usageItems.groupBy {
            it.packageName
        }.forEach { (s, list) ->
            val duration = list.sumOf { it.usageTimestamp }
            val filteredDuration = filterDuration(duration)
            if (filteredDuration != 0L) {
                expectedUsageDurationList.add(
                    UsageDurationDataItem(
                        appName = list.first().appName,
                        packageName = s,
                        usageDurationInMilliseconds = filteredDuration,
                        usageDuration = getDurationFromMilliseconds(filteredDuration),
                        appCategoryName = when (list.first().appCategory) {
                            UsageDataItem.Category.MAPS -> UiText.StringResource(R.string.category_maps)
                            UsageDataItem.Category.SOCIAL -> UiText.StringResource(R.string.category_social)
                            UsageDataItem.Category.PRODUCTIVITY -> UiText.StringResource(R.string.category_productivity)
                            UsageDataItem.Category.GAME -> UiText.StringResource(R.string.category_game)
                            UsageDataItem.Category.VIDEO -> UiText.StringResource(R.string.category_video)
                            UsageDataItem.Category.NEWS -> UiText.StringResource(R.string.category_news)
                            UsageDataItem.Category.OTHER -> UiText.StringResource(R.string.category_other)
                        },
                        appCategory = list.first().appCategory
                    )
                )
            }
        }
        val expectedTotalDuration =
            expectedUsageDurationList.sumOf { it.usageDurationInMilliseconds }
        val totalDuration = result.sumOf { it.usageDurationInMilliseconds }
        assertThat(totalDuration).isEqualTo(expectedTotalDuration)
    }

    @Test
    fun `usage duration for app properly calculated`() {
        val apps = listOf("myApp", "another app", "some app i forgot to uninstall", "bruh")
        val packageName = listOf(
            "com.ex.myApp",
            "hi.another app",
            "cu.some app i forgot to uninstall",
            "cri.bruh"
        )
        val usageItems = (1..33).map {
            val rnd = Random.nextInt(0, 3)
            UsageDataItem(
                appName = apps[rnd],
                packageName = packageName[rnd],
                usageTimestamp = Random.nextLong(),
                usageType = UsageDataItem.EventType.Start,
                appCategory = UsageDataItem.Category.PRODUCTIVITY,
            )
        }
        val result = calculateUsageDuration(usageItems, getDurationFromMilliseconds, filterDuration)
        val expectedUsageDurationList = ArrayList<UsageDurationDataItem>()
        usageItems.groupBy {
            it.packageName
        }.forEach { (s, list) ->
            val duration = list.sumOf { it.usageTimestamp }
            val filteredDuration = filterDuration(duration)
            if (filteredDuration != 0L) {
                expectedUsageDurationList.add(
                    UsageDurationDataItem(
                        appName = list.first().appName,
                        packageName = s,
                        usageDurationInMilliseconds = filteredDuration,
                        usageDuration = getDurationFromMilliseconds(filteredDuration),
                        appCategoryName = when (list.first().appCategory) {
                            UsageDataItem.Category.MAPS -> UiText.StringResource(R.string.category_maps)
                            UsageDataItem.Category.SOCIAL -> UiText.StringResource(R.string.category_social)
                            UsageDataItem.Category.PRODUCTIVITY -> UiText.StringResource(R.string.category_productivity)
                            UsageDataItem.Category.GAME -> UiText.StringResource(R.string.category_game)
                            UsageDataItem.Category.VIDEO -> UiText.StringResource(R.string.category_video)
                            UsageDataItem.Category.NEWS -> UiText.StringResource(R.string.category_news)
                            UsageDataItem.Category.OTHER -> UiText.StringResource(R.string.category_other)
                        },
                        appCategory = list.first().appCategory
                    )
                )
            }
        }
        val expectedAppDuration =
            expectedUsageDurationList.filter { it.packageName == packageName[0] }
                .firstOrNull()?.usageDuration
        val appDuration =
            result.filter { it.packageName == packageName[0] }.firstOrNull()?.usageDuration
        assertThat(appDuration).isEqualTo(expectedAppDuration)
    }


}