package ly.com.tahaben.launcher_presentation.wait

import android.annotation.SuppressLint
import android.content.Intent
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
import ly.com.tahaben.core_ui.components.DelayedLaunchOverlay
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.onboarding_presentaion.main.MainScreenViewModel
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class DelayedLaunchActivity : ComponentActivity() {

    companion object{
        const val PACKAGE_NAME = "PACKAGE_NAME"
    }

    private val viewModel by viewModels<DelayedLaunchViewModel>()
    @Inject
    lateinit var prefs: Preference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val delayDuration = runBlocking{ prefs.getDelayedLaunchDuration().first() }
        val packageName = intent.getStringExtra(PACKAGE_NAME)
        val pm = packageManager
        var appName = ""
        if (packageName != null) {
            viewModel.addLaunchAttempt(packageName)
            viewModel.getLaunchAttemptsForPackage(packageName)
            val appInfo = pm.getApplicationInfo(packageName, 0)
            appName = appInfo.loadLabel(pm).toString()
        }
        enableEdgeToEdge()
        setContent {
            val mainScreenViewModel= hiltViewModel<MainScreenViewModel>()
            val mainState = mainScreenViewModel.mainScreenState.collectAsState().value
            val isDarkMode = when (mainState.uiMode) {
                UIModeAppearance.DARK_MODE -> true
                UIModeAppearance.LIGHT_MODE -> false
                UIModeAppearance.FOLLOW_SYSTEM -> isSystemInDarkTheme()
            }
            val launchCount = viewModel.launchAttemptCount.collectAsState().value
            val state = viewModel.state.collectAsState().value
            FarhanTheme(
                darkMode = isDarkMode,
                colorStyle = mainState.themeColors
            ) {
                DelayedLaunchOverlay(
                    modifier = Modifier,
                    delayInSeconds = delayDuration,
                    launchCount = launchCount,
                    appName = appName,
                    delayMessage = state.selectedDelayedLaunchMessage.ifEmpty { state.delayedLaunchMessages.randomOrNull() ?: "" },
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
