package ly.com.tahaben.launcher_data.local.preferences

import android.content.SharedPreferences
import ly.com.tahaben.launcher_domain.preferences.Preference

class DefaultPreferences(
    private val sharePref: SharedPreferences
) : Preference {

    override fun isLauncherEnabled(): Boolean {
        return sharePref.getBoolean(Preference.KEY_LAUNCHER_ENABLED, false)
    }

    override fun setLauncherEnabled(isEnabled: Boolean) {
        sharePref.edit()
            .putBoolean(Preference.KEY_LAUNCHER_ENABLED, isEnabled)
            .apply()
    }
}