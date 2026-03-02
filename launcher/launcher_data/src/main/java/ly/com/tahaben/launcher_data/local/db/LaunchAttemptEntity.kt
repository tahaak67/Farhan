package ly.com.tahaben.launcher_data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 27/3/2025.
 */

@Entity
data class LaunchAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val timestamp: Long = System.currentTimeMillis()
)