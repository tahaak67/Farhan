package ly.com.tahaben.infinite_scroll_blocker_presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.InfiniteScrollUseCases
import javax.inject.Inject

@HiltViewModel
class InfiniteScrollOnBoardingViewModel @Inject constructor(
    private val infiniteScrollUseCases: InfiniteScrollUseCases
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onNextClick() {
        viewModelScope.launch {
            infiniteScrollUseCases.saveShouldShowOnBoarding(false)
            _uiEvent.send(UiEvent.Success)
        }
    }

}