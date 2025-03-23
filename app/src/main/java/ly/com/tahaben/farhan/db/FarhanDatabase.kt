package ly.com.tahaben.farhan.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ly.com.tahaben.launcher_data.local.AppEntity
import ly.com.tahaben.launcher_data.local.AppsDao
import ly.com.tahaben.launcher_data.local.TimeLimitDao
import ly.com.tahaben.launcher_data.local.TimeLimitEntity
import ly.com.tahaben.notification_filter_data.local.NotificationDao
import ly.com.tahaben.notification_filter_data.local.entity.NotificationItemEntity
import ly.com.tahaben.usage_overview_data.local.UsageDao
import ly.com.tahaben.usage_overview_data.local.entity.DayLastUpdatedEntity
import ly.com.tahaben.usage_overview_data.local.entity.LocalDateTypeConverter
import ly.com.tahaben.usage_overview_data.local.entity.UsageDataItemEntity

/* Created by Taha https://github.com/tahaak67/ at 15/9/2024 */

@Database(
    entities = [UsageDataItemEntity::class, NotificationItemEntity::class, AppEntity::class, TimeLimitEntity::class, DayLastUpdatedEntity::class],
    version = 1
)
@TypeConverters(LocalDateTypeConverter::class)
abstract class FarhanDatabase : RoomDatabase() {
    abstract val usageDao: UsageDao
    abstract val notificationDao: NotificationDao
    abstract val appDao: AppsDao
    abstract val timeLimitDao: TimeLimitDao
}