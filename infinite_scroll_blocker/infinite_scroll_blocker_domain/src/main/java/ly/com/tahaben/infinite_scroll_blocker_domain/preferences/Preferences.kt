package ly.com.tahaben.infinite_scroll_blocker_domain.preferences

interface Preferences {

    fun loadShouldShowOnBoarding(): Boolean
    fun saveShouldShowOnBoarding(shouldShow: Boolean)
    fun isServiceEnabled(): Boolean
    fun setServiceState(isEnabled: Boolean)
    fun savePackageToInfiniteScrollExceptions(packageName: String)
    fun removePackageFromInInfiniteScrollExceptions(packageName: String)
    fun isPackageInInfiniteScrollExceptions(packageName: String): Boolean
    fun getInInfiniteScrollExceptionsList(): Set<String>
    fun getInfiniteScrollTimeOut(): Int
    fun setInfiniteScrollTimeOut(minutes: Int)
    fun loadDarkModeOn(): String
    fun loadThemeColors(): String

    companion object {
        const val KEY_INFINITE_SCROLL_SERVICE_STATS = "infinite_scroll_service_stats"
        const val KEY_INFINITE_SCROLL_EXCEPTIONS = "infinite_scroll_exceptions"
        const val KEY_INFINITE_SCROLL_TIME_OUT = "infinite_scroll_time_out"
        const val KEY_INFINITE_SCROLL_SHOULD_SHOW_ON_BOARDING =
            "infinite_scroll_should_show_on_boarding"
        const val KEY_APP_DARK_MODE_ON = "app_dark_mode_on"
        const val KEY_APP_THEME_COLORS = "app_theme_colors"
    }
}