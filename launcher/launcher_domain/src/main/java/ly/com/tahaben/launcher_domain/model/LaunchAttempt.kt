package ly.com.tahaben.launcher_domain.model

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 27/3/2025.
 */

data class LaunchAttempt(
    val id: Long,
    val packageName: String,
    val timestamp: Long
)