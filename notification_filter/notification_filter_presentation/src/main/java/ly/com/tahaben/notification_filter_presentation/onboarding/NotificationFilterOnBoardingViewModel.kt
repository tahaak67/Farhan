package ly.com.tahaben.notification_filter_presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.notification_filter_domain.use_cases.NotificationFilterUseCases
import javax.inject.Inject

@HiltViewModel
class NotificationFilterOnBoardingViewModel @Inject constructor(
    private val notificationFilterUseCases: NotificationFilterUseCases
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onNextClick() {
        viewModelScope.launch {
            notificationFilterUseCases.saveShouldShowOnBoarding(false)
            _uiEvent.send(UiEvent.Success)
        }
    }

}