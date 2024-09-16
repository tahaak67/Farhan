package ly.com.tahaben.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ly.com.tahaben.domain.preferences.Preferences
import ly.com.tahaben.domain.use_case.GetDarkModePreference
import ly.com.tahaben.domain.use_case.GetThemeColorsPreference
import ly.com.tahaben.domain.use_case.IsMainSwitchState
import ly.com.tahaben.domain.use_case.LoadShouldShowCombineDbDialog
import ly.com.tahaben.domain.use_case.LoadShouldShowcaseAppearanceMenu
import ly.com.tahaben.domain.use_case.MainScreenUseCases
import ly.com.tahaben.domain.use_case.SaveDarkModePreference
import ly.com.tahaben.domain.use_case.SaveShouldShowcaseAppearanceMenu
import ly.com.tahaben.domain.use_case.SaveThemeColorsPreference
import ly.com.tahaben.domain.use_case.SetMainSwitchState
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainScreenModule {

    @Provides
    @Singleton
    fun provideMainScreenUseCases(preferences: Preferences): MainScreenUseCases {
        return MainScreenUseCases(
            getDarkModePreference = GetDarkModePreference(preferences),
            saveDarkModePreference = SaveDarkModePreference(preferences),
            isMainSwitchEnabled = IsMainSwitchState(preferences),
            setMainSwitchState = SetMainSwitchState(preferences),
            getThemeColorsPreference = GetThemeColorsPreference(preferences),
            saveThemeColorsPreference = SaveThemeColorsPreference(preferences),
            loadShouldShowcaseAppearanceMenu = LoadShouldShowcaseAppearanceMenu(preferences),
            saveShouldShowcaseAppearanceMenu = SaveShouldShowcaseAppearanceMenu(preferences),
            loadShouldShowCombineDbDialog = LoadShouldShowCombineDbDialog(preferences)
        )
    }
}