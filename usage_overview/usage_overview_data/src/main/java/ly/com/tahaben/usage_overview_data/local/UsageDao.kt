package ly.com.tahaben.usage_overview_data.local

import androidx.room.*
import ly.com.tahaben.usage_overview_data.local.entity.DayLastUpdatedEntity
import ly.com.tahaben.usage_overview_data.local.entity.UsageDataItemEntity
import java.time.LocalDate

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 09,Mar,2023
 */
@Dao
interface UsageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageItem(usageDataItemEntity: UsageDataItemEntity)

    @Query("SELECT * FROM usageDataItemEntity WHERE ABS(usage_timestamp) BETWEEN :from AND :to")
    suspend fun getUsageItemsForRange(from: Long, to: Long): List<UsageDataItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setLastDbUpdateTimeForDay(dayLastUpdatedEntity: DayLastUpdatedEntity)

    @Query("SELECT last_update_time FROM dayLastUpdatedEntity WHERE day = :day")
    suspend fun getLastDbUpdateTimeForDay(day: LocalDate): Long?

    @Query("DELETE FROM usageDataItemEntity WHERE date(ABS(usage_timestamp) / 1000, 'unixepoch') = :day")
    suspend fun deleteInfoForDay(day: LocalDate)

    @Query("DELETE FROM DayLastUpdatedEntity WHERE day = :day")
    suspend fun deleteLastDbUpdateTimeForDay(day: LocalDate)

    @Query("SELECT day FROM DayLastUpdatedEntity")
    suspend fun getUpdatedDays(): List<LocalDate>
}