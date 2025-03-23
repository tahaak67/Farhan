package ly.com.tahaben.launcher_domain.model


/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 16,Feb,2023
 */
data class TimeLimit(
    val packageName: String,
    val timeLimitInMilli: Long,
    val timeAtAddingInMilli: Long
)
