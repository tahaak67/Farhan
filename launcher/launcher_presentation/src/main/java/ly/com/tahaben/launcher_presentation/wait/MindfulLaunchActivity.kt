package ly.com.tahaben.launcher_presentation.wait

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core_ui.components.DelayedLaunchOverlay
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.onboarding_presentaion.main.MainScreenViewModel

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class MindfulLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                DelayedLaunchOverlay(
                    modifier = Modifier,
                    isDelayRunning = true,
                    openApp = {
                        moveTaskToBack(true)
                        finish()
                    },
                    dismissOverlay = {
                        startActivity(
                            Intent(Intent.ACTION_MAIN)
                                .addCategory(Intent.CATEGORY_HOME)
                                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                        )
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
        onBackPressedDispatcher.addCallback(this,callback)
    }

}
