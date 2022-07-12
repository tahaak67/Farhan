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
import ly.com.tahaben.notification_filter_presentation.settings.exceptions.NotificationFilterExceptionsScreen
import ly.com.tahaben.onboarding_presentaion.main.MainScreen
import ly.com.tahaben.screen_grayscale_domain.use_cases.GrayscaleUseCases
import ly.com.tahaben.screen_grayscale_presentation.GrayscaleScreen
import ly.com.tahaben.screen_grayscale_presentation.exceptions.GrayscaleWhiteListScreen
import ly.com.tahaben.screen_grayscale_presentation.onboarding.GrayscaleOnBoardingScreen
import ly.com.tahaben.usage_overview_presentation.UsageOverviewScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var grayscaleUseCases: GrayscaleUseCases

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isGrayscaleEnabled =
            grayscaleUseCases.isGrayscaleEnabled() && grayscaleUseCases.isAccessibilityPermissionGranted()
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
                                isGrayscaleEnabled = isGrayscaleEnabled,
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
                            if (grayscaleUseCases.loadShouldShowOnBoarding()) {
                                GrayscaleOnBoardingScreen(
                                    onNextClick = {
                                        navController.navigate(Routes.SCREEN_GRAY_SCALE) {
                                            popUpTo(Routes.MAIN)
                                        }
                                    }
                                )
                            } else {
                                GrayscaleScreen(
                                    onNavigateUp = { navController.navigateUp() },
                                    onNavigateToExceptions = { navController.navigate(Routes.SCREEN_GRAY_SCALE_WHITE_LIST) })
                            }
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
                        composable(Routes.NOTIFICATION_FILTER_EXCEPTIONS) {
                            NotificationFilterExceptionsScreen(
                                scaffoldState = scaffoldState,
                                onNavigateUp = { navController.navigateUp() },
                            )
                        }
                        composable(Routes.SCREEN_GRAY_SCALE_WHITE_LIST) {
                            GrayscaleWhiteListScreen(
                                scaffoldState = scaffoldState,
                                onNavigateUp = { navController.navigateUp() })
                        }
                    }
                }
            }
        }
    }
}