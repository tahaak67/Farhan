package ly.com.tahaben.onboarding_presentaion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.domain.preferences.Preferences
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val preferences: Preferences
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun setOnBoardingShown() {
        viewModelScope.launch {
            preferences.saveShouldShowOnBoarding(false)
            _uiEvent.send(UiEvent.Success)
        }
    }
}