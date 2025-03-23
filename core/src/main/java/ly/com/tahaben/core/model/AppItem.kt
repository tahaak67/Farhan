package ly.com.tahaben.core.model

data class AppItem(
    var name: String?,
    var packageName: String,
    var category: String? = null,
    var isException: Boolean = false,
    var isSystemApp: Boolean = false,
    val activityName: String = "",
    val userSerial: Long = 0L
)
