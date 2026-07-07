package ly.com.tahaben.screen_grayscale_domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences
import ly.com.tahaben.screen_grayscale_domain.use_cases.AskForAccessibilityPermission
import ly.com.tahaben.screen_grayscale_domain.use_cases.AskForSecureSettingsPermission
import ly.com.tahaben.screen_grayscale_domain.use_cases.GetAppGrayscaleState
import ly.com.tahaben.screen_grayscale_domain.use_cases.GetGrayscaleWhiteList
import ly.com.tahaben.screen_grayscale_domain.use_cases.GetInstalledAppsList
import ly.com.tahaben.screen_grayscale_domain.use_cases.GrayscaleUseCases
import ly.com.tahaben.screen_grayscale_domain.use_cases.IsAccessibilityPermissionGranted
import ly.com.tahaben.screen_grayscale_domain.use_cases.IsDeviceRooted
import ly.com.tahaben.screen_grayscale_domain.use_cases.IsGrayscaleEnabled
import ly.com.tahaben.screen_grayscale_domain.use_cases.IsSecureSettingsPermissionGranted
import ly.com.tahaben.screen_grayscale_domain.use_cases.LoadShouldShowOnBoarding
import ly.com.tahaben.screen_grayscale_domain.use_cases.SaveShouldShowOnBoarding
import ly.com.tahaben.screen_grayscale_domain.use_cases.SetAppGrayscaleState
import ly.com.tahaben.screen_grayscale_domain.use_cases.SetGrayscaleState
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
            AskForSecureSettingsPermission(grayscaleUtil),
            GetGrayscaleWhiteList(preferences),
            GetAppGrayscaleState(preferences, installedAppsRepo),
            SetAppGrayscaleState(preferences),
            IsGrayscaleEnabled(accessibilityUtils),
            IsSecureSettingsPermissionGranted(grayscaleUtil),
            SetGrayscaleState(grayscaleUtil),
            GetInstalledAppsList(installedAppsRepo),
            AskForAccessibilityPermission(accessibilityUtils),
            IsAccessibilityPermissionGranted(accessibilityUtils),
            IsDeviceRooted(grayscaleUtil)
        )
    }


}