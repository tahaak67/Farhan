package ly.com.tahaben.launcher_presentation.wait

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core_ui.components.DelayedUnlockOverlay
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.onboarding_presentaion.main.MainScreenViewModel
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class DelayedUnlockActivity : ComponentActivity() {

    private val viewModel by viewModels<DelayedLaunchViewModel>()

    @Inject
    lateinit var prefs: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val delayDuration = runBlocking { prefs.getDelayedUnlockDuration().first() }
        enableEdgeToEdge()
        setContent {
            val mainScreenViewModel = hiltViewModel<MainScreenViewModel>()
            val mainState = mainScreenViewModel.mainScreenState.collectAsState().value
            val isDarkMode = when (mainState.uiMode) {
                UIModeAppearance.DARK_MODE -> true
                UIModeAppearance.LIGHT_MODE -> false
                UIModeAppearance.FOLLOW_SYSTEM -> isSystemInDarkTheme()
            }
            val state = viewModel.state.collectAsState().value
            FarhanTheme(
                darkMode = isDarkMode,
                colorStyle = mainState.themeColors
            ) {
                DelayedUnlockOverlay(
                    modifier = Modifier,
                    delayInSeconds = delayDuration,
                    delayMessage = state.selectedDelayedLaunchMessage.ifEmpty { state.delayedLaunchMessages.randomOrNull() ?: "" },
                    onCountdownFinished = {
                        finish()
                    }
                )
            }
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable back press
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}
