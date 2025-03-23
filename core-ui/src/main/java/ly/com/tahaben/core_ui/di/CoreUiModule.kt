package ly.com.tahaben.core_ui.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.core_ui.use_cases.GetCurrentThemeColors
import ly.com.tahaben.core_ui.use_cases.IsDarkModeEnabled
import ly.com.tahaben.core_ui.use_cases.UiUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreUiModule {

    @Provides
    @Singleton
    fun provideCoreUiUseCases(sharedPreferences: SharedPreferences): UiUseCases{
        return UiUseCases(
            getCurrentThemeColors = GetCurrentThemeColors(sharedPreferences),
            isDarkModeEnabled = IsDarkModeEnabled(sharedPreferences)
        )
    }
}