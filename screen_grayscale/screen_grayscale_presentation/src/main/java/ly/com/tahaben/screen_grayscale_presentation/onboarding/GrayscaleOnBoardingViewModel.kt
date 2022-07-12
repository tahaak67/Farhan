package ly.com.tahaben.screen_grayscale_presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.screen_grayscale_domain.use_cases.GrayscaleUseCases
import javax.inject.Inject

@HiltViewModel
class GrayscaleOnBoardingViewModel @Inject constructor(
    private val grayscaleUseCases: GrayscaleUseCases
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onNextClick() {
        viewModelScope.launch {
            grayscaleUseCases.saveShouldShowOnBoarding(false)
            _uiEvent.send(UiEvent.Success)
        }
    }

}