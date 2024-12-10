package ly.com.tahaben.launcher_data.mapper

import ly.com.tahaben.launcher_data.local.TimeLimitEntity
import ly.com.tahaben.launcher_domain.model.TimeLimit

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 16,Feb,2023
 */

fun TimeLimitEntity.toTimeLimit(): TimeLimit =
    TimeLimit(packageName, timeLimitInMilli, timeAtAddingInMilli)


fun TimeLimit.toTimeLimitEntity(): TimeLimitEntity =
    TimeLimitEntity(packageName, timeLimitInMilli, timeAtAddingInMilli)