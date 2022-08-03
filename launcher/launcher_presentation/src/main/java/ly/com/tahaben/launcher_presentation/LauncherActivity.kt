package ly.com.tahaben.launcher_presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.launcher_domain.preferences.Preference
import javax.inject.Inject


@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {


    @Inject
    lateinit var launcherPref: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*if (!launcherPref.isLauncherEnabled()){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }*/
        setContent {
            FarhanTheme {
                LauncherScreen(
                    //navigateToMain = {startActivity(Intent(this, MainActivity::class.java))}
                )
            }
        }
    }
}