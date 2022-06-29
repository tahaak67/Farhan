package ly.com.tahaben.usage_overview_domain.model

import ly.com.tahaben.core.util.UiText

data class UsageDurationDataItem(
    val appName: String,
    val packageName: String,
    val usageDurationInMilliseconds: Long,
    val usageDuration: UiText,
    val appCategoryName: UiText,
    val appCategory: UsageDataItem.Category
    //val usageType: UsageDataItem.EventType
)
