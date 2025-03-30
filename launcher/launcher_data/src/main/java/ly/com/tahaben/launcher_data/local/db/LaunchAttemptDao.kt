package ly.com.tahaben.launcher_data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 27/3/2025.
 */
@Dao
interface LaunchAttemptDao {
    @Insert
    suspend fun insert(launchAttemptEntity: LaunchAttemptEntity)

    @Query("SELECT COUNT(*) FROM LaunchAttemptEntity WHERE packageName = :packageName AND timestamp BETWEEN :from AND :to")
    suspend fun getLaunchAttemptsForPackageWithinRange(from: Long, to: Long, packageName: String): Int

    @Query("SELECT COUNT(*) FROM LaunchAttemptEntity WHERE packageName = :packageName AND timestamp > :from")
    suspend fun getLaunchAttemptsForPackageAfter(from: Long, packageName: String): Int

    @Query("DELETE FROM LaunchAttemptEntity WHERE timestamp < :timestamp")
    suspend fun clearLaunchAttemptsEarlierThan(timestamp: Long): Int
}