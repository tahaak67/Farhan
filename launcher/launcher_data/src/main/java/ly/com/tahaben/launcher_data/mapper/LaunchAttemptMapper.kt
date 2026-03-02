package ly.com.tahaben.launcher_data.mapper

import ly.com.tahaben.launcher_data.local.db.LaunchAttemptEntity
import ly.com.tahaben.launcher_domain.model.LaunchAttempt

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 27/3/2025.
 */

fun LaunchAttemptEntity.toLaunchAttempt(): LaunchAttempt {
    return LaunchAttempt(
        id = id,
        packageName = packageName,
        timestamp = timestamp
    )
}

fun LaunchAttempt.toLaunchAttemptEntity(): LaunchAttemptEntity {
    return LaunchAttemptEntity(
        id = id,
        packageName = packageName,
        timestamp = timestamp
    )
}