package ly.com.tahaben.domain.preferences

interface Preferences {

    fun loadShouldShowOnBoarding(): Boolean
    fun saveShouldShowOnBoarding(shouldShow: Boolean)

    fun loadDarkModeOn(): String
    fun saveDarkModeOn(darkMode: String)

    fun loadMainSwitchState(): Boolean
    fun setMainSwitchState(switchState: Boolean)

    companion object {
        const val KEY_APP_SHOULD_SHOW_ON_BOARDING =
            "app_should_show_on_boarding"
        const val KEY_APP_DARK_MODE_ON = "app_dark_mode_on"
    }
}