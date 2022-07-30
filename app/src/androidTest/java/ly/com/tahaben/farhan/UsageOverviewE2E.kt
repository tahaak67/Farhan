package ly.com.tahaben.farhan

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import ly.com.tahaben.core.navigation.Routes
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.farhan.repository.UsageRepositoryFake
import ly.com.tahaben.onboarding_presentaion.OnBoardingScreen
import ly.com.tahaben.onboarding_presentaion.main.MainScreen
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.use_case.*
import ly.com.tahaben.usage_overview_presentation.UsageOverviewScreen
import ly.com.tahaben.usage_overview_presentation.UsageOverviewViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class UsageOverviewE2E {


    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var repositoryFake: UsageRepositoryFake
    private lateinit var usageOverviewUseCases: UsageOverviewUseCases
    private lateinit var usageOverviewViewModel: UsageOverviewViewModel
    private var cnt: Context? = null

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {

        repositoryFake = UsageRepositoryFake()
        usageOverviewUseCases = UsageOverviewUseCases(
            calculateUsageDuration = CalculateUsageDuration(),
            filterUsageEvents = FilterUsageEvents(),
            getUsageDataForDate = GetUsageDataForDate(repositoryFake),
            getDurationFromMilliseconds = GetDurationFromMilliseconds(),
            isDateToDay = IsDateToday(),
            isUsagePermissionGranted = IsUsagePermissionGranted(repositoryFake),
            filterDuration = FilterDuration()
        )
        usageOverviewViewModel = UsageOverviewViewModel(usageOverviewUseCases)
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
        composeRule.setContent {
            val scaffoldState = rememberScaffoldState()
            navController = rememberNavController()
            cnt = LocalContext.current
            FarhanTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.MAIN,
                    ) {
                        composable(Routes.WELCOME) {
                            OnBoardingScreen(
                                onNavigateToMain = {
                                    navController.navigate(Routes.MAIN) {
                                        popUpTo(Routes.WELCOME) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                        composable(Routes.MAIN) {
                            MainScreen(
                                tip = "A random tip for the test :)",
                                isGrayscaleEnabled = false,
                                isInfiniteScrollBlockerEnabled = false,
                                isNotificationFilterEnabled = false,
                                navController = navController,
                            )
                        }
                        composable(Routes.USAGE) {
                            UsageOverviewScreen(
                                onNavigateUp = { navController.navigateUp() },
                                viewModel = usageOverviewViewModel
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

        composeRule
            .onNodeWithText("Usage")
            .assertExists()
        composeRule
            .onNodeWithText("Usage")
            .performClick()
        assertThat(
            navController
                .currentDestination
                ?.route
                ?.startsWith(Routes.USAGE)
        ).isTrue()


        composeRule
            .onNodeWithText("100.0")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Farhan")
            .assertIsDisplayed()
        composeRule.onAllNodesWithText("Productivity")
            .assertCountEquals(2)
        composeRule.onAllNodesWithText("Productivity")[0]
            .assertIsDisplayed()
        composeRule.onAllNodesWithText("Productivity")[1]
            .assertIsDisplayed()
        composeRule.onNodeWithText("46m")
            .assertIsDisplayed()
        composeRule.onNodeWithText("46")
            .assertIsDisplayed()


        composeRule
            .onNodeWithContentDescription("Back")
            .performClick()
        assertThat(
            navController
                .currentDestination
                ?.route
                ?.startsWith(Routes.MAIN)
        ).isTrue()

    }

}