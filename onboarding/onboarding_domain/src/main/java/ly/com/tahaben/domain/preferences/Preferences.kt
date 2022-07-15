package ly.com.tahaben.domain.preferences

interface Preferences {

    fun loadShouldShowOnBoarding(): Boolean
    fun saveShouldShowOnBoarding(shouldShow: Boolean)


    companion object {
        const val KEY_APP_SHOULD_SHOW_ON_BOARDING =
            "app_should_show_on_boarding"
    }
}