package ly.com.tahaben.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core.model.UIModeAppearance.DARK_MODE
import ly.com.tahaben.core.model.UIModeAppearance.FOLLOW_SYSTEM
import ly.com.tahaben.core.model.UIModeAppearance.LIGHT_MODE
import ly.com.tahaben.domain.preferences.Preferences

class GetDarkModePreference(
    private val preferences: Preferences
) {
    operator suspend fun invoke(): Flow<UIModeAppearance> {
        return preferences.loadDarkModeStateAsFlow().map { uiMode ->
            when (uiMode) {
                UIModeAppearance.DARK_MODE.name -> DARK_MODE
                UIModeAppearance.LIGHT_MODE.name -> LIGHT_MODE
                else -> FOLLOW_SYSTEM
            }
        }

    }
}