package ly.com.tahaben.launcher_presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core.util.HomeWatcher
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.launcher_domain.preferences.Preference
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
            FarhanTheme(
                darkMode = false,
                colorStyle = ThemeColors.Classic
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

    override fun onStop() {
        super.onStop()
        homeWatcher.stopWatch()
    }

}