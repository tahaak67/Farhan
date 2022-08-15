package ly.com.tahaben.core.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import timber.log.Timber

class HomeWatcher(private val context: Context) {

    private var listener: OnHomePressedListener? = null
    private var receiver: InnerReceiver? = null
    private val filter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)

    fun setOnHomePressedListener(listener: OnHomePressedListener) {
        this.listener = listener
        receiver = InnerReceiver()
    }

    fun startWatch() {
        receiver?.let {
            context.registerReceiver(it, filter)
        }
    }

    fun stopWatch() {
        receiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: Exception) {
                Timber.e(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    inner class InnerReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            if (intent.action != Intent.ACTION_CLOSE_SYSTEM_DIALOGS) return
            val reason = intent.getStringExtra("reason") ?: return
            if (reason != "homekey") return
            listener?.onHomePressed()
        }
    }

    interface OnHomePressedListener {
        fun onHomePressed()
    }
}