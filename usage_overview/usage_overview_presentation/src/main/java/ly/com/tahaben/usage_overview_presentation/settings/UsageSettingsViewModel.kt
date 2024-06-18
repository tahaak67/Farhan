package ly.com.tahaben.usage_overview_presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.usage_overview_domain.preferences.Preferences
import ly.com.tahaben.usage_overview_domain.use_case.UsageSettingsUseCases
import ly.com.tahaben.usage_overview_domain.util.WorkerKeys
import javax.inject.Inject

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 06,May,2023
 */
@HiltViewModel
class UsageSettingsViewModel @Inject constructor(
    private val usageSettingsUseCases: UsageSettingsUseCases,
    private val preferences: Preferences
) : ViewModel() {

    var state by mutableStateOf(UsageSettingsState())
        private set

    private val _event = Channel<UiEventUsageSettings>()
    val event = _event.receiveAsFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    init {
        checkCachingEnabled()
        checkAutoCachingEnabled()
        getEnabledUsageReports()
        getSavedSettings()
    }

    private fun getSavedSettings() {
        state = state.copy(
            isIgnoreFarhan = preferences.isIgnoreFarhan(),
            isIgnoreLauncher = preferences.isIgnoreLauncher()
        )
    }

    fun setCachingEnabled(isEnabled: Boolean) {
        usageSettingsUseCases.setCachingEnabled(isEnabled)
        state = state.copy(
            isCacheEnabled = isEnabled
        )
        if (!isEnabled) {
            setAutoCachingEnabled(false)
        }
    }

    fun checkCachingEnabled() {
        state = state.copy(
            isCacheEnabled = usageSettingsUseCases.isCachingEnabled()
        )
    }

    fun setAutoCachingEnabled(isEnabled: Boolean) {
        usageSettingsUseCases.setAutoCachingEnabled(isEnabled)
        state = state.copy(
            isAutoCachingEnabled = isEnabled
        )
        if (isEnabled) {
            viewModelScope.launch {
                _event.send(UiEventUsageSettings.AutoCacheEnabled)
            }
        }
    }

    fun setWeeklyReportsEnabled(isEnabled: Boolean) {
        state = state.copy(
            isWeeklyReportsEnabled = isEnabled
        )
    }

    fun setMonthlyReportsEnabled(isEnabled: Boolean) {
        state = state.copy(
            isMonthlyReportsEnabled = isEnabled
        )
    }

    fun setYearlyReportsEnabled(isEnabled: Boolean) {
        state = state.copy(
            isYearlyReportsEnabled = isEnabled
        )
    }

    // saves user choice in persistent storage
    fun saveUsageReportsEnabled() {
        val reportsOptionsMap = mapOf(
            WorkerKeys.WEEKLY_USAGE_REPORTS to state.isWeeklyReportsEnabled,
            WorkerKeys.MONTHLY_USAGE_REPORTS to state.isMonthlyReportsEnabled,
            WorkerKeys.YEARLY_USAGE_REPORTS to state.isYearlyReportsEnabled
        )
        usageSettingsUseCases.setUsageReportsEnabled(reportsOptionsMap)
    }

    fun checkAutoCachingEnabled() {
        state = state.copy(
            isAutoCachingEnabled = usageSettingsUseCases.isAutoCachingEnabled()
        )
    }

    fun getEnabledUsageReports() {
        val reportsOptionsMap = usageSettingsUseCases.getEnabledUsageReports()
        state = state.copy(
            isWeeklyReportsEnabled = reportsOptionsMap[WorkerKeys.WEEKLY_USAGE_REPORTS] ?: false,
            isMonthlyReportsEnabled = reportsOptionsMap[WorkerKeys.MONTHLY_USAGE_REPORTS] ?: false,
            isYearlyReportsEnabled = reportsOptionsMap[WorkerKeys.YEARLY_USAGE_REPORTS] ?: false,
        )
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !state.visiblePermissionDialogQueue.contains(permission)) {
            state.visiblePermissionDialogQueue.add(permission)
        }
    }

    fun openAppSettings() {
        usageSettingsUseCases.openAppSettings()
    }

    fun onEvent(event: UsageSettingsEvent) {
        viewModelScope.launch {
            when (event) {
                UsageSettingsEvent.DismissSelectReportsDialog -> {
                    state = state.copy(
                        showSelectReportsDialog = false
                    )
                }

                UsageSettingsEvent.ShowSelectReportsDialog -> {
                    state = state.copy(
                        showSelectReportsDialog = true
                    )
                }

                UsageSettingsEvent.DismissPermissionDialog -> {
                    state.visiblePermissionDialogQueue.removeFirst()
                }

                is UsageSettingsEvent.OnIgnoreFarhanClick -> {
                    preferences.setIgnoreFarhan(event.ignoreFarhan)
                    state = state.copy(isIgnoreFarhan = event.ignoreFarhan)
                }
                is UsageSettingsEvent.OnIgnoreLauncherClick -> {
                    preferences.setIgnoreLauncher(event.ignoreLauncher)
                    state = state.copy(isIgnoreLauncher = event.ignoreLauncher)
                }
            }
        }
    }
}