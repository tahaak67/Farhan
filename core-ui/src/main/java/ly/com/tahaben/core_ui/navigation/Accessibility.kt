package ly.com.tahaben.core_ui.navigation

import android.content.Context
import android.content.Intent
import android.provider.Settings

fun openAccessibilitySettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}