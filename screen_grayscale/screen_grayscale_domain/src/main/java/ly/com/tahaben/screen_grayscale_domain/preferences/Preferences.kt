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
    fun savePackageToGrayscaleAgnosticList(packageName: String)
    fun removePackageFromGrayscaleAgnosticList(packageName: String)
    fun isPackageInGrayscaleAgnosticList(packageName: String): Boolean
    fun savePackageToGrayscaleColoredList(packageName: String)
    fun removePackageFromGrayscaleColoredList(packageName: String)
    fun isPackageInGrayscaleColoredList(packageName: String): Boolean


    companion object {
        const val KEY_GRAYSCALE_SERVICE_STATS = "grayscale_service_stats"
        const val KEY_GRAYSCALE_WHITE_LIST = "grayscale_white_list"
        const val KEY_GRAYSCALE_AGNOSTIC_LIST = "grayscale_agnostic_list"
        const val KEY_GRAYSCALE_COLORED_LIST = "grayscale_colored_list"
        const val KEY_GRAYSCALE_SHOULD_SHOW_ON_BOARDING = "grayscale_should_show_on_boarding"
    }
}