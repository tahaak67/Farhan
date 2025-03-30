package ly.com.tahaben.launcher_domain.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository
import ly.com.tahaben.launcher_domain.repository.TimeLimitRepository
import ly.com.tahaben.launcher_domain.use_case.launcher.CheckIfCurrentLauncher
import ly.com.tahaben.launcher_domain.use_case.launcher.GetInstalledActivities
import ly.com.tahaben.launcher_domain.use_case.launcher.IsLauncherEnabled
import ly.com.tahaben.launcher_domain.use_case.launcher.LaunchAppInfo
import ly.com.tahaben.launcher_domain.use_case.launcher.LaunchDefaultAlarmApp
import ly.com.tahaben.launcher_domain.use_case.launcher.LaunchDefaultCameraApp
import ly.com.tahaben.launcher_domain.use_case.launcher.LaunchDefaultDialer
import ly.com.tahaben.launcher_domain.use_case.launcher.LaunchMainActivityForApp
import ly.com.tahaben.launcher_domain.use_case.launcher.LauncherUseCases
import ly.com.tahaben.launcher_domain.use_case.launcher.LoadActivitiesFromDatabase
import ly.com.tahaben.launcher_domain.use_case.launcher.OpenDefaultLauncherSettings
import ly.com.tahaben.launcher_domain.use_case.launcher.RemoveAppFromDB
import ly.com.tahaben.launcher_domain.use_case.launcher.SetBlackWallpaper
import ly.com.tahaben.launcher_domain.use_case.launcher.SetLauncherEnabled
import ly.com.tahaben.launcher_domain.use_case.time_limit.AddPackageToTimeLimitWhiteList
import ly.com.tahaben.launcher_domain.use_case.time_limit.AddTimeLimitToDb
import ly.com.tahaben.launcher_domain.use_case.time_limit.AskForAccessibilityPermission
import ly.com.tahaben.launcher_domain.use_case.time_limit.AskForAppearOnTopPermission
import ly.com.tahaben.launcher_domain.use_case.time_limit.GetInstalledApps
import ly.com.tahaben.launcher_domain.use_case.time_limit.GetTimeLimitForPackage
import ly.com.tahaben.launcher_domain.use_case.time_limit.IsAccessibilityPermissionGranted
import ly.com.tahaben.launcher_domain.use_case.time_limit.IsAppearOnTopPermissionGranted
import ly.com.tahaben.launcher_domain.use_case.time_limit.IsPackageInTimeLimitWhiteList
import ly.com.tahaben.launcher_domain.use_case.time_limit.IsTimeLimiterEnabled
import ly.com.tahaben.launcher_domain.use_case.time_limit.RemovePackageFromTimeLimitWhiteList
import ly.com.tahaben.launcher_domain.use_case.time_limit.RemoveTimeLimitFromDb
import ly.com.tahaben.launcher_domain.use_case.time_limit.SetTimeLimiterEnabled
import ly.com.tahaben.launcher_domain.use_case.time_limit.StartTimeLimitService
import ly.com.tahaben.launcher_domain.use_case.time_limit.StopTimeLimitService
import ly.com.tahaben.launcher_domain.use_case.time_limit.TimeLimitUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LauncherDomainModule {

    @Provides
    @Singleton
    fun provideLauncherUseCases(
        activitiesRepository: AvailableActivitiesRepository,
        @ApplicationContext context: Context,
        sharedPref: Preference,
    ): LauncherUseCases {
        return LauncherUseCases(
            GetInstalledActivities(activitiesRepository),
            LoadActivitiesFromDatabase(activitiesRepository),
            CheckIfCurrentLauncher(context),
            OpenDefaultLauncherSettings(context),
            SetBlackWallpaper(context),
            LaunchMainActivityForApp(activitiesRepository),
            LaunchAppInfo(activitiesRepository),
            LaunchDefaultDialer(activitiesRepository),
            LaunchDefaultCameraApp(activitiesRepository),
            LaunchDefaultAlarmApp(activitiesRepository),
            SetLauncherEnabled(sharedPref),
            IsLauncherEnabled(sharedPref),
            RemoveAppFromDB(activitiesRepository)
        )
    }

    @Provides
    @Singleton
    fun provideTimeLimitUseCases(
        timeLimitRepository: TimeLimitRepository,
        sharedPref: Preference,
        installedAppsRepo: InstalledAppsRepository
    ): TimeLimitUseCases {
        return TimeLimitUseCases(
            AddTimeLimitToDb(timeLimitRepository),
            RemoveTimeLimitFromDb(timeLimitRepository),
            GetTimeLimitForPackage(timeLimitRepository),
            AddPackageToTimeLimitWhiteList(sharedPref),
            RemovePackageFromTimeLimitWhiteList(sharedPref),
            IsPackageInTimeLimitWhiteList(sharedPref),
            SetTimeLimiterEnabled(sharedPref),
            IsTimeLimiterEnabled(sharedPref),
            IsAccessibilityPermissionGranted(timeLimitRepository),
            AskForAccessibilityPermission(timeLimitRepository),
            IsAppearOnTopPermissionGranted(timeLimitRepository),
            AskForAppearOnTopPermission(timeLimitRepository),
            GetInstalledApps(installedAppsRepo),
            StartTimeLimitService(timeLimitRepository),
            StopTimeLimitService(timeLimitRepository)
        )
    }

}