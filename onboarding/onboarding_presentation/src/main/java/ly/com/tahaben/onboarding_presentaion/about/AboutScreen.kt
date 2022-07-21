package ly.com.tahaben.onboarding_presentaion.about

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.White
import ly.com.tahaben.core_ui.mirror

@Composable
fun AboutScreen(
    onNavigateUp: () -> Unit
) {

    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.about_app))
            },
            backgroundColor = White,
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        modifier = Modifier.mirror(),
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.spaceSmall)
        ) {
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Text(
                text = stringResource(R.string.about_app_content),
                style = MaterialTheme.typography.h4
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Text(
                text = stringResource(R.string.developer),
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Text(
                text = stringResource(R.string.taha_name_dev),
                style = MaterialTheme.typography.h4
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Text(
                text = stringResource(R.string.source_code),
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Text(
                text = stringResource(R.string.soon),
                style = MaterialTheme.typography.h4
            )
        }
    }

}