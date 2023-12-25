 package ly.com.tahaben.infinite_scroll_blocker_presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core_ui.components.OnBoardingContent

@Composable
fun InfiniteScrollOnBoardingScreen(
    onNextClick: () -> Unit,
    viewModel: InfiniteScrollOnBoardingViewModel = hiltViewModel()
) {


    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> onNextClick()
                else -> Unit
            }
        }
    }

    OnBoardingContent(
        message = stringResource(id = R.string.infinite_scroll_onboarding_message),
        onNextClick = viewModel::onNextClick,
        gifId = R.drawable.infinite_scrolling,
        gifDescription = stringResource(id = R.string.on_boarding_image_description)
    )
}