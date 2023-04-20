package ly.com.tahaben.usage_overview_data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 11,Mar,2023
 */
@Entity
data class DayLastUpdatedEntity(
    @PrimaryKey
    val day: LocalDate,
    @ColumnInfo(name = "last_update_time")
    val lastUpdateTime: Long
)
