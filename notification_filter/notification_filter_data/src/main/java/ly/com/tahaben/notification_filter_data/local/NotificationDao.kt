package ly.com.tahaben.notification_filter_data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ly.com.tahaben.notification_filter_data.local.entity.NotificationItemEntity

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationItem(notificationItemEntity: NotificationItemEntity)

    @Delete
    suspend fun deleteNotificationItem(notificationItemEntity: NotificationItemEntity)

    @Query("DELETE  FROM notificationitementity")
    suspend fun deleteAllNotifications()

    @Query("SELECT * FROM notificationitementity")
    fun getNotifications(): Flow<List<NotificationItemEntity>>

    @Query("SELECT * FROM notificationitementity")
    suspend fun getNotificationsAsList(): List<NotificationItemEntity>


}