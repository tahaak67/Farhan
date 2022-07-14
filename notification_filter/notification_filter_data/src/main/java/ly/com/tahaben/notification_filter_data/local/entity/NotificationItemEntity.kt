package ly.com.tahaben.notification_filter_data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationItemEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val appName: String?,
    val title: String?,
    val text: String?,
    val time: String,
    val creatorPackage: String
)
