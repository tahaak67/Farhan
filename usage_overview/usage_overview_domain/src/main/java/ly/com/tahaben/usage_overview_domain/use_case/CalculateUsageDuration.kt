package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.model.UsageDurationDataItem
import timber.log.Timber
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

class CalculateUsageDuration {

    operator fun invoke(
        usageDataItem: List<UsageDataItem>,
        getDurationFromMilliseconds: GetDurationFromMilliseconds
    ): List<UsageDurationDataItem> {
        val usageDurationList = arrayListOf<UsageDurationDataItem>()
        usageDataItem
            .groupBy { it.packageName }
            .forEach { (s, list) ->

                val duration = list.sumOf { it.usageTimestamp }
                Timber.d("durationinmilli not  filtered= $duration")
                val filteredDuration = filterDuration(duration)
                if (filteredDuration != 0L) {
                    usageDurationList.add(
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
        return usageDurationList
    }

    private fun filterDuration(milliseconds: Long): Long {
        val durationInMilli = milliseconds.milliseconds
        //this sometimes returns an unrealistic large number for hours until this is fixed i will have to manually set it to 0 if its larger than 24 (since we only get usage for 1 day at a time)
        return durationInMilli.toComponents { hours, minutes, _, _ ->
            val hrs = if (abs(hours) > 24L) 0L else hours
            val min = abs(minutes)
            val t = (((hrs * 60) + min) * 60 * 1000).milliseconds.inWholeMilliseconds
            Timber.d("durationinmilli= $t")
            t
        }
    }
}