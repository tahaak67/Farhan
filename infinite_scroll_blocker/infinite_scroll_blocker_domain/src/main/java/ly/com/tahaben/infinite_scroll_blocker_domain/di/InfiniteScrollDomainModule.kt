package ly.com.tahaben.infinite_scroll_blocker_domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.*
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
            LoadShouldShowOnBoarding(preferences)
        )
    }
}