package ly.com.tahaben.farhan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.core.navigation.Args
import ly.com.tahaben.core.navigation.Routes
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.domain.preferences.Preferences
import ly.com.tahaben.farhan.db.DatabaseCombineHelper
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.InfiniteScrollUseCases
import ly.com.tahaben.infinite_scroll_blocker_presentation.InfiniteScrollingBlockerScreen
import ly.com.tahaben.infinite_scroll_blocker_presentation.exceptions.InfiniteScrollExceptionsScreen
import ly.com.tahaben.infinite_scroll_blocker_presentation.onboarding.InfiniteScrollOnBoardingScreen
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.launcher_presentation.settings.LauncherSettingsScreen
import ly.com.tahaben.launcher_presentation.time_limiter.TimeLimiterSettingsScreen
import ly.com.tahaben.launcher_presentation.time_limiter.TimeLimiterWhitelistScreen
import ly.com.tahaben.launcher_presentation.wait.MindfulLaunchScreen
import ly.com.tahaben.launcher_presentation.wait.MindfulLaunchViewModel
import ly.com.tahaben.launcher_presentation.wait.MindfulLaunchWhiteListScreen
import ly.com.tahaben.notification_filter_domain.use_cases.NotificationFilterUseCases
import ly.com.tahaben.notification_filter_presentation.NotificationFilterScreen
import ly.com.tahaben.notification_filter_presentation.onboarding.NotificationFilterOnBoardingScreen
import ly.com.tahaben.notification_filter_presentation.settings.NotificationFilterSettingsScreen
import ly.com.tahaben.notification_filter_presentation.settings.exceptions.NotificationFilterExceptionsScreen
import ly.com.tahaben.onboarding_presentaion.OnBoardingScreen
import ly.com.tahaben.onboarding_presentaion.SelectAppearanceScreen
import ly.com.tahaben.onboarding_presentaion.about.AboutScreen
import ly.com.tahaben.onboarding_presentaion.main.MainScreen
import ly.com.tahaben.onboarding_presentaion.main.MainScreenEvent
import ly.com.tahaben.onboarding_presentaion.main.MainScreenViewModel
import ly.com.tahaben.screen_grayscale_domain.use_cases.GrayscaleUseCases
import ly.com.tahaben.screen_grayscale_presentation.GrayscaleScreen
import ly.com.tahaben.screen_grayscale_presentation.exceptions.GrayscaleWhiteListScreen
import ly.com.tahaben.screen_grayscale_presentation.onboarding.GrayscaleOnBoardingScreen
import ly.com.tahaben.usage_overview_presentation.UsageOverviewScreen
import ly.com.tahaben.usage_overview_presentation.settings.UsageSettingsScreen
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var grayscaleUseCases: GrayscaleUseCases

    @Inject
    lateinit var infiniteScrollUseCases: InfiniteScrollUseCases

    @Inject
    lateinit var notificationFilterUseCases: NotificationFilterUseCases

    @Inject
    lateinit var onBoardingPref: Preferences

    @Inject
    lateinit var launcherPref: Preference

    @Inject
    lateinit var databaseCombineHelper: DatabaseCombineHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shouldShowOnBoarding = onBoardingPref.loadShouldShowOnBoarding()
        var shouldShowSelectThemeScreen: Boolean
        runBlocking {
            shouldShowSelectThemeScreen = (onBoardingPref.loadThemeColors() == "Unknown")
        }
        val tip = getTip()
        val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        setContent {
            val mainScreenViewModel = hiltViewModel<MainScreenViewModel>()
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
                val navController = rememberNavController()
                val snackbarHostState = remember {
                    SnackbarHostState()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) {
                    it.calculateBottomPadding()
                    NavHost(
                        navController = navController,
                        startDestination = if (shouldShowOnBoarding) {
                            Routes.WELCOME
                        } else if (shouldShowSelectThemeScreen) {
                            Routes.SELECT_THEME
                        } else {
                            Routes.MAIN
                        },
                    ) {
                        composable(Routes.WELCOME) {
                            OnBoardingScreen(
                                onFinishOnBoarding = {
                                    navController.navigate(Routes.SELECT_THEME) {
                                        popUpTo(Routes.WELCOME) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                        composable(Routes.SELECT_THEME) {
                            SelectAppearanceScreen(
                                state = mainState,
                                onEvent = mainScreenViewModel::onEvent,
                                onOkClick = {
                                    navController.navigate(Routes.MAIN) {
                                        popUpTo(Routes.SELECT_THEME) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                        composable(Routes.MAIN) {
                            val mainScreenUiEvent =
                                mainScreenViewModel.uiEvent.collectAsState(
                                    initial = UiEvent.HideSnackBar
                                ).value
                            MainScreen(
                                tip = tip,
                                isGrayscaleEnabled = grayscaleUseCases.isGrayscaleEnabled() &&
                                        grayscaleUseCases.isAccessibilityPermissionGranted(),
                                isInfiniteScrollBlockerEnabled = infiniteScrollUseCases.isServiceEnabled() &&
                                        infiniteScrollUseCases.isAccessibilityPermissionGranted(),
                                isNotificationFilterEnabled = notificationFilterUseCases.checkIfNotificationServiceIsEnabled() &&
                                        notificationFilterUseCases.checkIfNotificationAccessIsGranted(),
                                isLauncherEnabled = launcherPref.isLauncherEnabled(),
                                navController = navController,
                                onEvent = { event ->
                                    when (event) {
                                        MainScreenEvent.OnCombineDbAgreeClick -> {
                                            mainScreenViewModel.onEvent(event)
                                            coroutineScope.launch {
                                                val result =
                                                    databaseCombineHelper.combineDatabases()
                                                mainScreenViewModel.onEvent(MainScreenEvent.OnDismissCombineDbDialog)
                                                if (result) {
                                                    snackbarHostState.showSnackbar(getString(R.string.db_migration_sucsessful))
                                                } else {
                                                    snackbarHostState.showSnackbar(getString(R.string.db_migration_fail))
                                                    finish()
                                                }
                                            }
                                        }

                                        MainScreenEvent.OnExitApp -> finish()
                                        else -> mainScreenViewModel.onEvent(event)
                                    }
                                },
                                state = mainState,
                                snackbarHostState = snackbarHostState,
                                uiEvent = mainScreenUiEvent
                            )
                        }
                        composable(
                            Routes.USAGE,
                            deepLinks = listOf(navDeepLink {
                                uriPattern =
                                    "app://$packageName/${Routes.USAGE}/{${Args.START_DATE}}/{${Args.END_DATE}}"
                                action = Intent.ACTION_VIEW

                            },
                                navDeepLink {
                                    uriPattern =
                                        "app://$packageName/${Routes.USAGE}/{${Args.START_DATE}}"
                                    action = Intent.ACTION_VIEW
                                }),
                            arguments = listOf(
                                navArgument(Args.START_DATE) {
                                    type = NavType.StringType
                                    nullable = true
                                },
                                navArgument(Args.END_DATE) {
                                    type = NavType.StringType
                                    nullable = true
                                }
                            )
                        ) { backStackEntry ->
                            val startDate = backStackEntry.arguments?.getString(Args.START_DATE)
                            val endDate = backStackEntry.arguments?.getString(Args.END_DATE)
                            Timber.d("args: $startDate")
                            UsageOverviewScreen(
                                onNavigateUp = navController::navigateUp,
                                onNavigateToSettings = { navController.navigate(Routes.USAGE_SETTINGS) },
                                scaffoldState = snackbarHostState,
                                startDate = startDate,
                                endDate = endDate
                            )
                        }
                        composable(Routes.USAGE_SETTINGS) {
                            UsageSettingsScreen(
                                onNavigateUp = { navController.navigateUp() },
                                shouldShowRational = ::shouldShowRational,
                                scaffoldState = snackbarHostState
                            )
                        }
                        composable(Routes.INFINITE_SCROLLING) {
                            if (infiniteScrollUseCases.loadShouldShowOnBoarding()) {
                                InfiniteScrollOnBoardingScreen(
                                    onNextClick = {
                                        navController.navigate(Routes.INFINITE_SCROLLING) {
                                            popUpTo(Routes.MAIN)
                                        }
                                    }
                                )
                            } else {
                                InfiniteScrollingBlockerScreen(
                                    onNavigateUp = { navController.navigateUp() },
                                    onNavigateToExceptions = { navController.navigate(Routes.INFINITE_SCROLLING_EXCEPTIONS) }
                                )
                            }
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
                                    onNavigateToExceptions = { navController.navigate(Routes.SCREEN_GRAY_SCALE_WHITE_LIST) },
                                    scaffoldState = snackbarHostState
                                )
                            }
                        }
                        composable(Routes.NOTIFICATION_FILTER, deepLinks = listOf(
                            navDeepLink {
                                uriPattern =
                                    "app://$packageName/${Routes.NOTIFICATION_FILTER}"
                                action = Intent.ACTION_VIEW
                            }
                        )) {
                            if (notificationFilterUseCases.loadShouldShowOnBoarding()) {
                                NotificationFilterOnBoardingScreen(
                                    onNextClick = {
                                        navController.navigate(Routes.NOTIFICATION_FILTER) {
                                            popUpTo(Routes.MAIN)
                                        }
                                    }
                                )
                            } else {
                                NotificationFilterScreen(
                                    navigateToNotificationSettings = {
                                        navController.navigate(Routes.NOTIFICATION_FILTER_SETTINGS)
                                    },
                                    onNavigateUp = {
                                        navController.navigateUp()
                                    },
                                    isUiModeDark = isDarkMode
                                )
                            }

                        }
                        composable(Routes.NOTIFICATION_FILTER_SETTINGS) {
                            NotificationFilterSettingsScreen(
                                onNavigateToExceptions = { navController.navigate(Routes.NOTIFICATION_FILTER_EXCEPTIONS) },
                                onNavigateUp = { navController.navigateUp() },
                                shouldShowRational = ::shouldShowRational,
                                snackbarHostState = snackbarHostState,
                                isDarkMode = isDarkMode
                            )
                        }
                        composable(Routes.INFINITE_SCROLLING_EXCEPTIONS) {
                            InfiniteScrollExceptionsScreen(
                                scaffoldState = snackbarHostState,
                                onNavigateUp = { navController.navigateUp() },
                            )
                        }
                        composable(Routes.NOTIFICATION_FILTER_EXCEPTIONS) {
                            NotificationFilterExceptionsScreen(
                                snackbarHostState = snackbarHostState,
                                onNavigateUp = { navController.navigateUp() },
                            )
                        }
                        composable(Routes.SCREEN_GRAY_SCALE_WHITE_LIST) {
                            GrayscaleWhiteListScreen(
                                scaffoldState = snackbarHostState,
                                onNavigateUp = { navController.navigateUp() })
                        }
                        composable(Routes.ABOUT_APP) {
                            AboutScreen(
                                onNavigateUp = { navController.navigateUp() },
                                versionName = BuildConfig.VERSION_NAME,
                                versionCode = BuildConfig.VERSION_CODE
                            )
                        }
                        composable(Routes.LAUNCHER_SETTINGS) {
                            LauncherSettingsScreen(
                                onNavigateUp = { navController.navigateUp() },
                                onNavigateToTimeLimiter = { navController.navigate(Routes.TimeLimiter_SETTINGS) },
                                onNavigateToMindfulLaunch = { navController.navigate(Routes.MINDFUL_LAUNCH_SETTINGS)}
                            )
                        }
                        composable(Routes.TimeLimiter_SETTINGS) {
                            TimeLimiterSettingsScreen(
                                onNavigateUp = { navController.navigateUp() },
                                onNavigateToWhitelist = { navController.navigate(Routes.TimeLimiter_WHITELIST_SETTINGS) })
                        }
                        composable(Routes.TimeLimiter_WHITELIST_SETTINGS) {
                            TimeLimiterWhitelistScreen(
                                snackbarHostState = snackbarHostState,
                                onNavigateUp = { navController.navigateUp() })
                        }
                        composable(Routes.MINDFUL_LAUNCH_SETTINGS) {
                            val viewModel: MindfulLaunchViewModel = hiltViewModel()
                            MindfulLaunchScreen(
                                onNavigateUp = { navController.navigateUp() },
                                onNavigateToWhitelist = { navController.navigate(Routes.MINDFUL_LAUNCH_WHITELIST) },
                                onEvent = viewModel::onEvent,
                                state = viewModel.state.collectAsStateWithLifecycle().value
                            )
                        }
                        composable(Routes.MINDFUL_LAUNCH_WHITELIST) {
                            val viewModel: MindfulLaunchViewModel = hiltViewModel()
                            MindfulLaunchWhiteListScreen(
                                onNavigateUp = { navController.navigateUp() },
                                onEvent = viewModel::onEvent,
                                state = viewModel.state.collectAsStateWithLifecycle().value
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getTip(): String {
        val array: Array<String> = this.resources.getStringArray(R.array.tips)
        return array[Random.nextInt(array.size)]
    }

    private fun shouldShowRational(permission: String): Boolean {
        return shouldShowRequestPermissionRationale(
            permission
        )
    }
}