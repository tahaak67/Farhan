package ly.com.tahaben.launcher_data.local

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 11,Jan,2023
 */
@Entity(tableName = "apps_db", primaryKeys = ["app_name", "package_name"])
data class AppEntity(

    @field:ColumnInfo(name = "app_name")
    val appName: String,

    @field:ColumnInfo(name = "package_name")
    val packageName: String,

    @field:ColumnInfo(name = "activity_name")
    val activityName: String,

    @field:ColumnInfo(name = "user_serial")
    val userSerial: Long
)
