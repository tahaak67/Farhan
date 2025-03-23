package ly.com.tahaben.launcher_data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 16,Feb,2023
 */
@Entity
data class TimeLimitEntity(
    @PrimaryKey(autoGenerate = false)
    val packageName: String,
    val timeLimitInMilli: Long,
    val timeAtAddingInMilli: Long = System.currentTimeMillis()
)
