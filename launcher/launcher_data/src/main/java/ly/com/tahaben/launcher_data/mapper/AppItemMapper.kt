package ly.com.tahaben.launcher_data.mapper

import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.launcher_data.local.db.AppEntity

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 11,Jan,2023
 */

fun AppItem.toAppEntity(): AppEntity {
    return AppEntity(
        appName = name ?: "",
        packageName = packageName,
        activityName = activityName,
        userSerial = userSerial
    )
}

fun AppEntity.toAppItem(): AppItem {
    return AppItem(
        name = appName,
        packageName = packageName,
        activityName = activityName,
        userSerial = userSerial
    )
}