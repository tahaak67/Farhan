package ly.com.tahaben.usage_overview_domain.model

data class UsageDataItem(
    val appName: String,
    val packageName: String,
    val usageTimestamp: Long,
    val usageType: EventType,
    val appCategory: Category? = null
) {
    enum class EventType {
        Start, Stop, Other
    }

    enum class Category {
        SOCIAL, PRODUCTIVITY, GAME, VIDEO, NEWS, MAPS, OTHER
    }
}
