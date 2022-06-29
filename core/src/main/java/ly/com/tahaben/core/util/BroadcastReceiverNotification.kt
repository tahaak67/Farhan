package ly.com.tahaben.core.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import ly.com.tahaben.core.R
import timber.log.Timber

const val NOTIFICATION_ID = 101
const val TITLE_EXTRA = "title_Extra"
const val MESSAGE_EXTRA = "message_Extra"

class BroadcastReceiverNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pm = context.packageManager

        val intentt = pm.getLaunchIntentForPackage("ly.com.tahaben.farhan")?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate", NOTIFICATION_ID)
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intentt, PendingIntent.FLAG_IMMUTABLE)

        val notification =
            NotificationCompat.Builder(context, context.getString(R.string.notify_me_channel_id))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(intent.getStringExtra(TITLE_EXTRA))
                .setContentText(intent.getStringExtra(MESSAGE_EXTRA))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)

        val action = intent.getStringExtra("action")
        if (action == "showFN") {
            Timber.d("action performed :)")
        }
    }
}