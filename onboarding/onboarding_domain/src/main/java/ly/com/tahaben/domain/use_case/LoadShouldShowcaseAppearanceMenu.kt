package ly.com.tahaben.domain.use_case

import ly.com.tahaben.domain.preferences.Preferences

class LoadShouldShowcaseAppearanceMenu(private val preferences: Preferences) {
    operator fun invoke(): Boolean {
        return preferences.loadShouldShowcaseAppearanceMenu()
    }
}
