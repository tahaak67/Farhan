@file:Suppress("UnnecessaryVariable")

package ly.com.tahaben.onboarding_data.preferences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import ly.com.tahaben.domain.preferences.Preferences

class DefaultPreferences(
    private val sharedPref: SharedPreferences
) : Preferences {

    override fun loadShouldShowOnBoarding(): Boolean {
        return sharedPref.getBoolean(Preferences.KEY_APP_SHOULD_SHOW_ON_BOARDING, true)
    }

    @SuppressLint("ApplySharedPref")
    override fun saveShouldShowOnBoarding(shouldShow: Boolean) {
        sharedPref.edit()
            .putBoolean(Preferences.KEY_APP_SHOULD_SHOW_ON_BOARDING, shouldShow)
            .commit()
    }


}