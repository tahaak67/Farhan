package ly.com.tahaben.domain.use_case

import kotlinx.coroutines.flow.Flow
import ly.com.tahaben.domain.preferences.Preferences

class IsMainSwitchState(private val preferences: Preferences) {
    suspend operator fun invoke(): Flow<Boolean> {
        return preferences.loadMainSwitchState()
    }
}
