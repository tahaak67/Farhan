package ly.com.tahaben.screen_grayscale_presentation.onboarding

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core_ui.components.OnBoardingContent

@Composable
fun GrayscaleOnBoardingScreen(
    onNextClick: () -> Unit,
    viewModel: GrayscaleOnBoardingViewModel = hiltViewModel()
) {


    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> onNextClick()
                else -> Unit
            }
        }
    }

    Scaffold { paddingValues ->
        OnBoardingContent(
            modifier = Modifier.padding(paddingValues),
            message = stringResource(id = R.string.grayscale_onboarding_message),
            onNextClick = viewModel::onNextClick,
            gifId = R.drawable.grayscale,
            gifDescription = stringResource(id = R.string.on_boarding_image_description)
        )
    }


}