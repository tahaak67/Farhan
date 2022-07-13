package ly.com.tahaben.infinite_scroll_blocker_presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.InfiniteScrollUseCases
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfiniteScrollBlockerViewModel @Inject constructor(
    private val infiniteScrollUseCases: InfiniteScrollUseCases
) : ViewModel() {

    var state by mutableStateOf(InfiniteScrollBlockerState())
        private set

    init {
        checkServiceStats()
        getTimeoutDuration()
    }

    fun checkServiceStats() {
        checkAccessibilityPermissionStats()
        state = state.copy(
            isServiceEnabled = infiniteScrollUseCases.isServiceEnabled() && infiniteScrollUseCases.isAccessibilityPermissionGranted()
        )
    }

    private fun checkAccessibilityPermissionStats() {
        state = state.copy(
            isAccessibilityPermissionGranted = infiniteScrollUseCases.isAccessibilityPermissionGranted()
        )
        Timber.d("state = ${state.isAccessibilityPermissionGranted}")
    }

    fun setServiceStats(isEnabled: Boolean) {
        infiniteScrollUseCases.setServiceState(isEnabled)
        if (!state.isAccessibilityPermissionGranted) {
            infiniteScrollUseCases.askForAccessibilityPermission()
        }
        state = state.copy(
            isServiceEnabled = isEnabled
        )
    }

    fun setTimeoutDuration(minutes: Int) {
        infiniteScrollUseCases.setTimeOutDuration(minutes)
        state = state.copy(
            timeoutDuration = minutes
        )
    }

    private fun getTimeoutDuration() {
        state = state.copy(
            timeoutDuration = infiniteScrollUseCases.getTimeOutDuration()
        )
    }

    fun checkIfAppearOnTopPermissionGranted() {
        state = state.copy(
            isAppearOnTopPermissionGranted = infiniteScrollUseCases.isAppearOnTopPermissionGranted()
        )
    }

    fun askForAppearOnTopPermission() {
        infiniteScrollUseCases.askForAppearOnTopPermission()
    }

    fun askForAccessibilityPermission() {
        infiniteScrollUseCases.askForAccessibilityPermission()
    }

}