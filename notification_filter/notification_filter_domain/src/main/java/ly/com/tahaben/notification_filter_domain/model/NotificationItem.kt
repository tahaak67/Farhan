package ly.com.tahaben.notification_filter_domain.model

data class NotificationItem(
    val id: String,
    val appName: String?,
    val title: String?,
    val text: String?,
    val time: String,
    val packageName: String,
)
