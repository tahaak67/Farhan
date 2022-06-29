package ly.com.tahaben.notification_filter_data.mapper

import android.app.Notification
import android.service.notification.StatusBarNotification
import ly.com.tahaben.notification_filter_data.local.entity.NotificationItemEntity
import ly.com.tahaben.notification_filter_domain.model.NotificationItem
import java.text.SimpleDateFormat
import java.util.*

fun NotificationItem.toNotificationEntity(): NotificationItemEntity {


    return NotificationItemEntity(
        id = id,
        title = title,
        text = text,
        time = time,
        creatorPackage = packageName
    )
}

fun NotificationItemEntity.toNotificationItem(): NotificationItem {

    return NotificationItem(
        id = id,
        title = title,
        text = text,
        time = time,
        packageName = creatorPackage
    )
}

/*
Not needed for now
fun Notification.toNotificationEntity(): NotificationItemEntity{
    val notification = this
    val extras = notification.extras
    val title = extras?.getString(Notification.EXTRA_TITLE)?.toString()
    val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()
    val progress = extras?.getInt(Notification.EXTRA_PROGRESS)
    val progressMax = extras?.getInt(Notification.EXTRA_PROGRESS_MAX)
    val progressIndeterminate = extras?.getBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE)

    return NotificationItemEntity(
        title = title,
        text = text,
        progress = progress,
        progressMax = progressMax,
        progressIndeterminate = progressIndeterminate,
        pendingIntent = notification.contentIntent
    )
}*/

fun StatusBarNotification.toNotificationItem(): NotificationItem {
    val notification = notification
    val extras = notification.extras
    val title = extras?.getString(Notification.EXTRA_TITLE)?.toString()
    val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val time = sdf.format(postTime)



    return NotificationItem(
        id = key,
        title = title,
        text = text,
        time = time,
        packageName = packageName ?: "Unknown"
    )
}