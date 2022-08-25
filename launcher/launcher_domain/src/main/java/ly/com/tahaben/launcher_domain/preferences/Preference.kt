package ly.com.tahaben.launcher_domain.preferences

interface Preference {
    fun isLauncherEnabled(): Boolean
    fun setLauncherEnabled(isEnabled: Boolean)

    companion object {
        const val KEY_LAUNCHER_ENABLED = "key_launcher_enabled"
    }
}