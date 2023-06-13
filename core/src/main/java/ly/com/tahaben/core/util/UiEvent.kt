package ly.com.tahaben.core.util

sealed class UiEvent {
    data class Navigate(val route: String) : UiEvent()
    object Success : UiEvent()
    object NavigateUp : UiEvent()
    object Loading : UiEvent()
    data class ShowSnackbar(val message: UiText) : UiEvent()
    object HideSnackBar : UiEvent()
    object ShowBottomSheet : UiEvent()
    object DismissBottomSheet : UiEvent()

}
