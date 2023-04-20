package ly.com.tahaben.usage_overview_data.mapper

import android.app.usage.UsageEvents
import android.content.pm.ApplicationInfo
import ly.com.tahaben.usage_overview_data.local.entity.UsageDataItemEntity
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem

fun UsageEvents.Event.toUsageDataItem(
    appName: String,
    appCategory: Int
): UsageDataItem {
    val packageName = packageName
    val usageTimestamp = timeStamp
    val usageType = when (eventType) {
        1 -> UsageDataItem.EventType.Start
        2 -> UsageDataItem.EventType.Stop
        else -> UsageDataItem.EventType.Other
    }
    val category = when (appCategory) {
        ApplicationInfo.CATEGORY_AUDIO -> UsageDataItem.Category.OTHER
        ApplicationInfo.CATEGORY_GAME -> UsageDataItem.Category.GAME
        ApplicationInfo.CATEGORY_PRODUCTIVITY -> UsageDataItem.Category.PRODUCTIVITY
        ApplicationInfo.CATEGORY_SOCIAL -> UsageDataItem.Category.SOCIAL
        ApplicationInfo.CATEGORY_VIDEO -> UsageDataItem.Category.VIDEO
        ApplicationInfo.CATEGORY_MAPS -> UsageDataItem.Category.MAPS
        ApplicationInfo.CATEGORY_NEWS -> UsageDataItem.Category.NEWS
        else -> UsageDataItem.Category.OTHER
    }
    return UsageDataItem(
        appName = appName,
        packageName = packageName,
        usageTimestamp = if (usageType == UsageDataItem.EventType.Start) (usageTimestamp * -1) else usageTimestamp,
        usageType = usageType,
        appCategory = category
    )
}

fun UsageEvents.Event.toUsageDataItem(
    appName: String
): UsageDataItem {
    val packageName = packageName
    val usageTimestamp = timeStamp
    val usageType = when (eventType) {
        1 -> UsageDataItem.EventType.Start
        2 -> UsageDataItem.EventType.Stop
        else -> UsageDataItem.EventType.Other
    }

    return UsageDataItem(
        appName = appName,
        packageName = packageName,
        usageTimestamp = if (usageType == UsageDataItem.EventType.Start) (usageTimestamp * -1) else usageTimestamp,
        usageType = usageType
    )
}

fun UsageDataItemEntity.toUsageDataItem(): UsageDataItem {
    return UsageDataItem(
        appName = appName,
        packageName = packageName,
        usageTimestamp = usageTimestamp,
        usageType = usageType,
        appCategory = appCategory
    )
}

fun UsageDataItem.toUsageDataItemEntity(): UsageDataItemEntity {
    return UsageDataItemEntity(
        appName = appName,
        packageName = packageName,
        usageTimestamp = usageTimestamp,//if (usageType == UsageDataItem.EventType.Start) (usageTimestamp * -1) else usageTimestamp,
        usageType = usageType,
        appCategory = appCategory
    )
}