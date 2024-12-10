package ly.com.tahaben.farhan

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core.navigation.Routes
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.farhan.repository.UsageRepositoryFake
import ly.com.tahaben.farhan.repository.WorkerRepositoryFake
import ly.com.tahaben.onboarding_presentaion.OnBoardingScreen
import ly.com.tahaben.onboarding_presentaion.main.MainScreen
import ly.com.tahaben.onboarding_presentaion.main.MainScreenViewModel
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.preferences.Preferences
import ly.com.tahaben.usage_overview_domain.use_case.CacheUsageDataForDate
import ly.com.tahaben.usage_overview_domain.use_case.CalculateUsageDuration
import ly.com.tahaben.usage_overview_domain.use_case.DeleteCacheForDay
import ly.com.tahaben.usage_overview_domain.use_case.FilterDuration
import ly.com.tahaben.usage_overview_domain.use_case.FilterUsageEvents
import ly.com.tahaben.usage_overview_domain.use_case.GetDurationFromMilliseconds
import ly.com.tahaben.usage_overview_domain.use_case.GetEnabledUsageReports
import ly.com.tahaben.usage_overview_domain.use_case.GetUpdatedDays
import ly.com.tahaben.usage_overview_domain.use_case.GetUsageDataForDate
import ly.com.tahaben.usage_overview_domain.use_case.GetUsageEventsFromDb
import ly.com.tahaben.usage_overview_domain.use_case.IsAutoCachingEnabled
import ly.com.tahaben.usage_overview_domain.use_case.IsCachingEnabled
import ly.com.tahaben.usage_overview_domain.use_case.IsDateToday
import ly.com.tahaben.usage_overview_domain.use_case.IsDayDataFullyUpdated
import ly.com.tahaben.usage_overview_domain.use_case.IsDayOver
import ly.com.tahaben.usage_overview_domain.use_case.IsUsagePermissionGranted
import ly.com.tahaben.usage_overview_domain.use_case.MergeDaysUsageDuration
import ly.com.tahaben.usage_overview_domain.use_case.OpenAppSettings
import ly.com.tahaben.usage_overview_domain.use_case.SetAutoCachingEnabled
import ly.com.tahaben.usage_overview_domain.use_case.SetCachingEnabled
import ly.com.tahaben.usage_overview_domain.use_case.SetUsageReportsEnabled
import ly.com.tahaben.usage_overview_domain.use_case.UsageOverviewUseCases
import ly.com.tahaben.usage_overview_domain.use_case.UsageSettingsUseCases
import ly.com.tahaben.usage_overview_presentation.UsageOverviewScreen
import ly.com.tahaben.usage_overview_presentation.UsageOverviewViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.DecimalFormat
import javax.inject.Inject

@HiltAndroidTest
class UsageOverviewE2E {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var repositoryFake: UsageRepositoryFake
    private lateinit var workerRepositoryFake: WorkerRepositoryFake
    private lateinit var usageOverviewUseCases: UsageOverviewUseCases
    private lateinit var usageSettingsUseCases: UsageSettingsUseCases
    private lateinit var usageOverviewViewModel: UsageOverviewViewModel

    @Inject
    lateinit var usagePreferences: Preferences

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        hiltRule.inject()
        repositoryFake = UsageRepositoryFake()
        workerRepositoryFake = WorkerRepositoryFake()
        usageOverviewUseCases = UsageOverviewUseCases(
            calculateUsageDuration = CalculateUsageDuration(),
            filterUsageEvents = FilterUsageEvents(),
            cacheUsageDataForDate = CacheUsageDataForDate(repositoryFake),
            getDurationFromMilliseconds = GetDurationFromMilliseconds(),
            isDateToDay = IsDateToday(),
            isUsagePermissionGranted = IsUsagePermissionGranted(repositoryFake),
            filterDuration = FilterDuration(),
            getUsageDataForDate = GetUsageDataForDate(repositoryFake),
            deleteCacheForDay = DeleteCacheForDay(repositoryFake),
            getUsageEventsFromDb = GetUsageEventsFromDb(repositoryFake),
            getUpdatedDays = GetUpdatedDays(repositoryFake),
            isDayDataFullyUpdated = IsDayDataFullyUpdated(repositoryFake),
            mergeDaysUsageDuration = MergeDaysUsageDuration(),
            isDayOver = IsDayOver()
        )
        usageSettingsUseCases = UsageSettingsUseCases(
            openAppSettings = OpenAppSettings(workerRepositoryFake),
            getEnabledUsageReports = GetEnabledUsageReports(usagePreferences),
            isAutoCachingEnabled = IsAutoCachingEnabled(usagePreferences),
            isCachingEnabled = IsCachingEnabled(usagePreferences),
            setUsageReportsEnabled = SetUsageReportsEnabled(workerRepositoryFake),
            setAutoCachingEnabled = SetAutoCachingEnabled(workerRepositoryFake),
            setCachingEnabled = SetCachingEnabled(usagePreferences),
        )
        usageOverviewViewModel =
            UsageOverviewViewModel(usageOverviewUseCases, usageSettingsUseCases, usagePreferences)
        repositoryFake.usageItems.add(
            UsageDataItem(
                appName = "Farhan",
                packageName = "ly.farhan",
                usageTimestamp = -1659166113695,
                usageType = UsageDataItem.EventType.Start,
                appCategory = UsageDataItem.Category.PRODUCTIVITY
            )
        )
        repositoryFake.usageItems.add(
            UsageDataItem(
                appName = "Farhan",
                packageName = "ly.farhan",
                usageTimestamp = 1659168892650,
                usageType = UsageDataItem.EventType.Stop,
                appCategory = UsageDataItem.Category.PRODUCTIVITY
            )
        )
        composeRule.activity.setContent {
            val mainScreenViewModel: MainScreenViewModel = hiltViewModel()
            val mainState = mainScreenViewModel.mainScreenState.collectAsStateWithLifecycle().value
            val snackbarHostState = remember {
                SnackbarHostState()
            }
            navController = rememberNavController()
            FarhanTheme(
                colorStyle = ThemeColors.Classic,
                darkMode = false
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) {
                    NavHost(
                        modifier = Modifier.padding(it),
                        navController = navController,
                        startDestination = Routes.MAIN,
                    ) {
                        composable(Routes.WELCOME) {
                            OnBoardingScreen(
                                onFinishOnBoarding = {
                                    navController.navigate(Routes.MAIN) {
                                        popUpTo(Routes.WELCOME) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                        composable(Routes.MAIN) {
                            val mainScreenUiEvent =
                                mainScreenViewModel.uiEvent.collectAsStateWithLifecycle(
                                    initialValue = UiEvent.HideSnackBar
                                ).value
                            MainScreen(
                                tip = "A random tip for the test :)",
                                isGrayscaleEnabled = false,
                                isInfiniteScrollBlockerEnabled = false,
                                isNotificationFilterEnabled = false,
                                navController = navController,
                                isLauncherEnabled = false,
                                snackbarHostState = snackbarHostState,
                                state = mainState,
                                onEvent = mainScreenViewModel::onEvent,
                                uiEvent = mainScreenUiEvent
                            )
                        }
                        composable(Routes.USAGE) {
                            UsageOverviewScreen(
                                onNavigateUp = { navController.navigateUp() },
                                viewModel = usageOverviewViewModel,
                                scaffoldState = snackbarHostState,
                                onNavigateToSettings = { navController.navigate(Routes.USAGE_SETTINGS) }
                            )
                        }

                    }
                }
            }
        }
    }

    @Test
    fun usageOverview_properlyCalculates_displaysData() {
        repositoryFake.permissionGranted = true
        val context = composeRule.activity.applicationContext

        // format the numbers to the device locale to avoid flaky tests ex: on some locales 100.0 is ١٠٠٫٠ and this will make the test fail
        val decimalFormatPercentage = DecimalFormat("#,###.0")
        val decimalFormat = DecimalFormat.getInstance()
        val productivityPercentageString = decimalFormatPercentage.format(100.0)
        val appItemUsageString = UiText.MixedString(46, R.string.minutes).asString(context)
        val totalUsageMinutesString = decimalFormat.format(46)

        composeRule
            .onNodeWithText(context.getString(R.string.usage))
            .assertExists()
        composeRule
            .onNodeWithText(context.getString(R.string.usage))
            .performClick()
        assertThat(
            navController
                .currentDestination
                ?.route
                ?.startsWith(Routes.USAGE)
        ).isTrue()


        composeRule
            .onNodeWithText(productivityPercentageString)
            .assertIsDisplayed()
        composeRule.onNodeWithText("Farhan")
            .assertIsDisplayed()
        composeRule.onAllNodesWithText(context.getString(R.string.category_productivity))
            .assertCountEquals(2)
        composeRule.onAllNodesWithText(context.getString(R.string.category_productivity))[0]
            .assertIsDisplayed()
        composeRule.onAllNodesWithText(context.getString(R.string.category_productivity))[1]
            .assertIsDisplayed()
        composeRule.onNodeWithText(appItemUsageString)
            .assertIsDisplayed()
        composeRule.onNodeWithText(totalUsageMinutesString)
            .assertIsDisplayed()

        composeRule
            .onNodeWithContentDescription(context.getString(R.string.back))
            .performClick()
        assertThat(
            navController
                .currentDestination
                ?.route
                ?.startsWith(Routes.MAIN)
        ).isTrue()

    }

}