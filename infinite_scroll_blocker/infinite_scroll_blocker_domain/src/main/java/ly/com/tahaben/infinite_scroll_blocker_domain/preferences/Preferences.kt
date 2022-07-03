package ly.com.tahaben.infinite_scroll_blocker_domain.preferences

interface Preferences {

    fun isServiceEnabled(): Boolean
    fun setServiceState(isEnabled: Boolean)
    fun savePackageToInfiniteScrollExceptions(packageName: String)
    fun removePackageFromInInfiniteScrollExceptions(packageName: String)
    fun isPackageInInfiniteScrollExceptions(packageName: String): Boolean
    fun getInInfiniteScrollExceptionsList(): Set<String>
    fun getInfiniteScrollTimeOut(): Int
    fun setInfiniteScrollTimeOut(minutes: Int)

    companion object {
        const val KEY_INFINITE_SCROLL_SERVICE_STATS = "infinite_scroll_service_stats"
        const val KEY_INFINITE_SCROLL_EXCEPTIONS = "infinite_scroll_exceptions"
        const val KEY_INFINITE_SCROLL_TIME_OUT = "infinite_scroll_time_out"
    }
}