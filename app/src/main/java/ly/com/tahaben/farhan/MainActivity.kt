package ly.com.tahaben.farhan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ly.com.tahaben.core.navigation.Routes
import ly.com.tahaben.core.util.NOTIFICATION_ID
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.infinite_scroll_blocker_presentation.InfiniteScrollingBlockerScreen
import ly.com.tahaben.infinite_scroll_blocker_presentation.exceptions.InfiniteScrollExceptionsScreen
import ly.com.tahaben.notification_filter_presentation.NotificationFilterScreen
import ly.com.tahaben.notification_filter_presentation.settings.NotificationFilterSettingsScreen
import ly.com.tahaben.onboarding_presentaion.main.MainScreen
import ly.com.tahaben.usage_overview_presentation.UsageOverviewScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FarhanTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (intent.getIntExtra(
                                "navigate",
                                -1
                            ) == NOTIFICATION_ID
                        ) {
                            Routes.NOTIFICATION_FILTER
                        } else {
                            Routes.MAIN
                        },
                    ) {
                        composable(Routes.WELCOME) {

                        }
                        composable(Routes.MAIN) {
                            MainScreen(
                                navController = navController,
                            )
                        }
                        composable(Routes.USAGE) {
                            UsageOverviewScreen(
                                onNavigateUp = { navController.navigateUp() }
                            )
                        }
                        composable(Routes.INFINITE_SCROLLING) {
                            //startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), 0)
                            InfiniteScrollingBlockerScreen(
                                onNavigateUp = { navController.navigateUp() },
                                onNavigateToExceptions = { navController.navigate(Routes.INFINITE_SCROLLING_EXCEPTIONS) }
                            )
                        }
                        composable(Routes.SCREEN_GRAY_SCALE) {

                        }
                        composable(Routes.NOTIFICATION_FILTER) {
                            NotificationFilterScreen(
                                navigateToNotificationSettings = {
                                    navController.navigate(Routes.NOTIFICATION_FILTER_SETTINGS)
                                }
                            )
                        }
                        composable(Routes.NOTIFICATION_FILTER_SETTINGS) {
                            NotificationFilterSettingsScreen(
                                onNavigateToExceptions = { navController.navigate(Routes.NOTIFICATION_FILTER_EXCEPTIONS) },
                                onNavigateUp = { navController.navigateUp() }
                            )
                        }
                        composable(Routes.INFINITE_SCROLLING_EXCEPTIONS) {
                            InfiniteScrollExceptionsScreen(
                                scaffoldState = scaffoldState,
                                onNavigateUp = { navController.navigateUp() },
                            )
                        }
                    }
                }
            }
        }
    }

    fun checkAccessibilityServiceState() {

    }
}