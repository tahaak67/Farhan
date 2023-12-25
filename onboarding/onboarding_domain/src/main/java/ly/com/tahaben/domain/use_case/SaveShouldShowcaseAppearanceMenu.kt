package ly.com.tahaben.domain.use_case

import ly.com.tahaben.domain.preferences.Preferences

class SaveShouldShowcaseAppearanceMenu(private val preferences: Preferences) {
    operator fun invoke(shouldShowcaseAppearanceMenu: Boolean) {
        preferences.saveShouldShowcaseAppearanceMenu(shouldShowcaseAppearanceMenu)
    }
}
