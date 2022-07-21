package ly.com.tahaben.core_ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing

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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.spaceLarge, vertical = spacing.spaceLarge),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        GifImage(
            modifier = Modifier
                .height(330.dp)
                .align(CenterHorizontally),
            gifId = gifId,
            gifDescription = gifDescription,
        )
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        Text(
            modifier = Modifier
                .wrapContentSize(),
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