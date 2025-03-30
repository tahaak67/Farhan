package ly.com.tahaben.launcher_presentation.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core.util.HomeWatcher
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.onboarding_presentaion.main.MainScreenViewModel
import javax.inject.Inject


@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {


    @Inject
    lateinit var launcherPref: Preference

    private lateinit var homeWatcher: HomeWatcher


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeWatcher = HomeWatcher(this)
        setContent {
            val mainScreenViewModel= hiltViewModel<MainScreenViewModel>()
            val mainState = mainScreenViewModel.mainScreenState.collectAsState().value
            val isDarkMode = when (mainState.uiMode) {
                UIModeAppearance.DARK_MODE -> true
                UIModeAppearance.LIGHT_MODE -> false
                UIModeAppearance.FOLLOW_SYSTEM -> isSystemInDarkTheme()
            }
            FarhanTheme(
                darkMode = isDarkMode,
                colorStyle = mainState.themeColors
            ) {
                LauncherScreen(
                    homeWatcher
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        homeWatcher.startWatch()
    }

    override fun onPause() {
        super.onPause()
        homeWatcher.stopWatch()
    }

}