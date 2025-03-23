package ly.com.tahaben.launcher_data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 16,Feb,2023
 */
@Dao
interface TimeLimitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAppTimeLimit(timeLimitEntity: TimeLimitEntity)

    @Update
    suspend fun updateAppTimeLimit(timeLimitEntity: TimeLimitEntity)

    @Delete
    suspend fun deleteAppTimeLimit(timeLimitEntity: TimeLimitEntity)

    @Query("SELECT * FROM timelimitentity WHERE packageName = :packageName LIMIT 1")
    suspend fun getTimeLimitForApp(packageName: String): TimeLimitEntity?

}