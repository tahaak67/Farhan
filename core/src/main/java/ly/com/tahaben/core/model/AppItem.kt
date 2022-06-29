package ly.com.tahaben.core.model

data class AppItem(
    var name: String?,
    var pckg: String,
    var category: String? = null,
    var isException: Boolean = false,
    var isSystemApp: Boolean = false,
)
