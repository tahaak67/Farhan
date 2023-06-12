package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.model.UsageDurationDataItem
import timber.log.Timber

class MergeDaysUsageDuration {

    operator fun invoke(
        usageDataItem: List<UsageDurationDataItem>,
        getDurationFromMilliseconds: GetDurationFromMilliseconds
    ): List<UsageDurationDataItem> {
        val usageDurationList = arrayListOf<UsageDurationDataItem>()
        usageDataItem
            .groupBy { it.packageName }
            .forEach { (s, list) ->

                val duration = list.sumOf { it.usageDurationInMilliseconds }
                Timber.d("durationinmilli not  filtered= $duration")
                val filteredDuration = duration
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
                                else -> UiText.DynamicString("")
                            },
                            appCategory = list.first().appCategory
                        )
                    )
                }
            }
        return usageDurationList
    }
}