package ly.com.tahaben.launcher_domain.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository
import ly.com.tahaben.launcher_domain.use_case.*

@Module
@InstallIn(ViewModelComponent::class)
object LauncherDomainModule {

    @Provides
    @ViewModelScoped
    fun provideLauncherUseCases(
        activitiesRepository: AvailableActivitiesRepository,
        @ApplicationContext context: Context
    ): LauncherUseCases {
        return LauncherUseCases(
            GetInstalledActivities(activitiesRepository),
            CheckIfCurrentLauncher(context),
            OpenDefaultLauncherSettings(context),
            SetBlackWallpaper(context)
        )
    }

}