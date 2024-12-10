package ly.com.tahaben.launcher_data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 11,Jan,2023
 */

@Dao
interface AppsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(appEntity: AppEntity)

    @Delete
    suspend fun removeDeletedApp(appEntity: AppEntity)

    @Query("SELECT * FROM apps_db ORDER BY app_name ASC")
    fun getInstalledActivities(): Flow<List<AppEntity>>
}