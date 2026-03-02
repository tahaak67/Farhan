package ly.com.tahaben.farhan.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ly.com.tahaben.launcher_data.local.db.AppEntity
import ly.com.tahaben.launcher_data.local.db.AppsDao
import ly.com.tahaben.launcher_data.local.db.LaunchAttemptDao
import ly.com.tahaben.launcher_data.local.db.LaunchAttemptEntity
import ly.com.tahaben.launcher_data.local.db.TimeLimitDao
import ly.com.tahaben.launcher_data.local.db.TimeLimitEntity
import ly.com.tahaben.notification_filter_data.local.NotificationDao
import ly.com.tahaben.notification_filter_data.local.entity.NotificationItemEntity
import ly.com.tahaben.usage_overview_data.local.UsageDao
import ly.com.tahaben.usage_overview_data.local.entity.DayLastUpdatedEntity
import ly.com.tahaben.usage_overview_data.local.entity.LocalDateTypeConverter
import ly.com.tahaben.usage_overview_data.local.entity.UsageDataItemEntity

/* Created by Taha https://github.com/tahaak67/ at 15/9/2024 */

@Database(
    entities = [UsageDataItemEntity::class, NotificationItemEntity::class, AppEntity::class, TimeLimitEntity::class, DayLastUpdatedEntity::class, LaunchAttemptEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(LocalDateTypeConverter::class)
abstract class FarhanDatabase : RoomDatabase() {
    abstract val usageDao: UsageDao
    abstract val notificationDao: NotificationDao
    abstract val appDao: AppsDao
    abstract val timeLimitDao: TimeLimitDao
    abstract val launchAttemptDao: LaunchAttemptDao
}