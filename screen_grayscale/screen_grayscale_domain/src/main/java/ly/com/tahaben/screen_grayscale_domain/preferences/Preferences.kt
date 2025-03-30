package ly.com.tahaben.screen_grayscale_domain.preferences

interface Preferences {

    fun loadShouldShowOnBoarding(): Boolean
    fun saveShouldShowOnBoarding(shouldShow: Boolean)
    suspend fun isGrayscaleEnabled(): Boolean
    fun setServiceState(isEnabled: Boolean)
    fun savePackageToInfiniteScrollExceptions(packageName: String)
    fun removePackageFromInInfiniteScrollExceptions(packageName: String)
    fun isPackageInInfiniteScrollExceptions(packageName: String): Boolean
    fun getInInfiniteScrollExceptionsList(): Set<String>


    companion object {
        const val KEY_GRAYSCALE_SERVICE_STATS = "grayscale_service_stats"
        const val KEY_GRAYSCALE_WHITE_LIST = "grayscale_white_list"
        const val KEY_GRAYSCALE_SHOULD_SHOW_ON_BOARDING = "grayscale_should_show_on_boarding"
    }
}