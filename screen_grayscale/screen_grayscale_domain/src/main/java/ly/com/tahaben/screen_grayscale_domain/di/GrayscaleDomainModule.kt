package ly.com.tahaben.screen_grayscale_domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.*
import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences
import ly.com.tahaben.screen_grayscale_domain.use_cases.*
import ly.com.tahaben.screen_grayscale_domain.util.AccessibilityServiceUtils
import ly.com.tahaben.screen_grayscale_domain.util.GrayscaleUtil
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object GrayscaleDomainModule {

    @Provides
    @Singleton
    fun provideGrayscaleUseCases(
        preferences: Preferences,
        installedAppsRepo: InstalledAppsRepository,
        grayscaleUtil: GrayscaleUtil,
        accessibilityUtils: AccessibilityServiceUtils
    ): GrayscaleUseCases {
        return GrayscaleUseCases(
            SaveShouldShowOnBoarding(preferences),
            LoadShouldShowOnBoarding(preferences),
            AddPackageToGrayscaleWhiteList(preferences),
            AskForSecureSettingsPermission(grayscaleUtil),
            GetGrayscaleWhiteList(preferences),
            IsPackageInGrayscaleWhiteList(preferences),
            IsGrayscaleEnabled(accessibilityUtils),
            IsSecureSettingsPermissionGranted(grayscaleUtil),
            RemovePackageFromGrayscaleWhiteList(preferences),
            SetGrayscaleState(preferences),
            GetInstalledAppsList(installedAppsRepo),
            AskForAccessibilityPermission(accessibilityUtils),
            IsAccessibilityPermissionGranted(accessibilityUtils),
            IsDeviceRooted(grayscaleUtil)
        )
    }


}