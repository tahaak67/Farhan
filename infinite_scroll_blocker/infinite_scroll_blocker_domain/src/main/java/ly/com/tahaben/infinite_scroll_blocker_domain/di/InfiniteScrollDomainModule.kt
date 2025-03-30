package ly.com.tahaben.infinite_scroll_blocker_domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.AddPackageToInfiniteScrollExceptions
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.AskForAccessibilityPermission
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.AskForAppearOnTopPermission
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.GetCountDown
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.GetDialogMessage
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.GetInfiniteScrollExceptions
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.GetInstalledAppsList
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.GetTimeOutDuration
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.InfiniteScrollUseCases
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.IsAccessibilityPermissionGranted
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.IsAppearOnTopPermissionGranted
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.IsPackageInInfiniteScrollExceptions
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.IsServiceEnabled
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.LoadShouldShowOnBoarding
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.RemovePackageFromInfiniteScrollExceptions
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.SaveShouldShowOnBoarding
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.SetServiceState
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.SetTimeOutDuration
import ly.com.tahaben.infinite_scroll_blocker_domain.util.AccessibilityServiceUtils
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object InfiniteScrollDomainModule {

    @Provides
    @Singleton
    fun provideInfiniteScrollUseCases(
        accessibilityUtil: AccessibilityServiceUtils,
        preferences: Preferences,
        installedAppsRepo: InstalledAppsRepository
    ): InfiniteScrollUseCases {
        return InfiniteScrollUseCases(
            AddPackageToInfiniteScrollExceptions(preferences),
            AskForAccessibilityPermission(accessibilityUtil),
            AskForAppearOnTopPermission(accessibilityUtil),
            GetInfiniteScrollExceptions(preferences),
            IsPackageInInfiniteScrollExceptions(preferences),
            IsServiceEnabled(accessibilityUtil),
            IsAccessibilityPermissionGranted(accessibilityUtil),
            IsAppearOnTopPermissionGranted(accessibilityUtil),
            RemovePackageFromInfiniteScrollExceptions(preferences),
            SetServiceState(preferences),
            SetTimeOutDuration(preferences),
            GetTimeOutDuration(preferences),
            GetInstalledAppsList(installedAppsRepo),
            SaveShouldShowOnBoarding(preferences),
            LoadShouldShowOnBoarding(preferences),
            GetCountDown(preferences),
            GetDialogMessage(preferences)
        )
    }
}