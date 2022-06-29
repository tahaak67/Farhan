package ly.com.tahaben.notification_filter_data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ly.com.tahaben.notification_filter_data.local.entity.NotificationItemEntity

@Database(
    entities = [NotificationItemEntity::class],
    version = 1
)
abstract class NotificationDatabase : RoomDatabase() {
    abstract val dao: NotificationDao
}