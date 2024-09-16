package ly.com.tahaben.farhan.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ly.com.tahaben.notification_filter_data.local.NotificationDao
import ly.com.tahaben.notification_filter_data.local.entity.NotificationItemEntity

// TODO: Remove on v 1.0
@Database(
    entities = [NotificationItemEntity::class],
    version = 1
)
abstract class NotificationDatabase : RoomDatabase() {
    abstract val dao: NotificationDao
}