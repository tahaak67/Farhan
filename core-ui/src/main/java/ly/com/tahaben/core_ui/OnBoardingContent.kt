package ly.com.tahaben.core_ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ly.com.tahaben.core.R

@Composable
fun OnBoardingContent(
    modifier: Modifier = Modifier,
    message: String,
    onNextClick: () -> Unit,
    @DrawableRes gifId: Int,
    gifDescription: String
) {

    val spacing = LocalSpacing.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.spaceLarge, vertical = spacing.spaceLarge),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        GifImage(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            gifId = gifId,
            gifDescription = gifDescription,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = message,
            style = MaterialTheme.typography.h4
        )
        Button(
            modifier = Modifier
                .align(Alignment.End),
            onClick = onNextClick,
        ) {
            Text(
                text = stringResource(id = R.string.next),
                style = MaterialTheme.typography.button
            )
        }
    }
}