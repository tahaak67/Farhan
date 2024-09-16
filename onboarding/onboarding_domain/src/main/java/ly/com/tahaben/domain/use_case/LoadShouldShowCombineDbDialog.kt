package ly.com.tahaben.domain.use_case

import ly.com.tahaben.domain.preferences.Preferences

class LoadShouldShowCombineDbDialog(private val preferences: Preferences) {
    operator fun invoke(): Boolean {
        return preferences.loadShouldCombineDb()
    }
}
