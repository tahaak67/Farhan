package ly.com.tahaben.usage_overview_data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 09,Mar,2023
 */
@Entity
data class UsageDataItemEntity(
    @ColumnInfo(name = "app_name")
    val appName: String,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "usage_timestamp")
    val usageTimestamp: Long,
    @ColumnInfo(name = "usage_type")
    val usageType: UsageDataItem.EventType,
    @ColumnInfo(name = "app_category")
    val appCategory: UsageDataItem.Category? = null
)
