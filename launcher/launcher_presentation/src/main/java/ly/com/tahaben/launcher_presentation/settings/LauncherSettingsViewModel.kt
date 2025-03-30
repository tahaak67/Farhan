package ly.com.tahaben.launcher_presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ly.com.tahaben.launcher_domain.use_case.launcher.LauncherUseCases
import javax.inject.Inject

@HiltViewModel
class LauncherSettingsViewModel @Inject constructor(
    private val launcherUseCases: LauncherUseCases
) : ViewModel() {

    var state by mutableStateOf(LauncherSettingsState())
        private set

    init {
        checkLauncherStats()
        checkDefaultLauncher()
    }


    fun checkLauncherStats() {
        state = state.copy(
            isLauncherEnabled = launcherUseCases.checkIfCurrentLauncher(),
        )
    }

    fun openLauncherSettings() {
        launcherUseCases.openDefaultLauncherSettings()
        launcherUseCases.setBlackWallpaper()
    }

    private fun checkDefaultLauncher() {
    }
}