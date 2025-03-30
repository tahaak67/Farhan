package ly.com.tahaben.onboarding_presentaion.about

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.mirror
import ly.com.tahaben.core_ui.theme.FarhanTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateUp: () -> Unit,
    versionName: String,
    versionCode: Int
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.about_app))
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
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
                .padding(horizontal = spacing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium)
        ) {
            Text(
                text = stringResource(R.string.about_app_content),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(R.string.developer),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.taha_name_dev),
                style = MaterialTheme.typography.headlineMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.spaceMedium)) {
                TextButton(onClick = {
                    safeOpenUri("https://www.linkedin.com/in/tahabenly/", context, clipboardManager)
                }) {
                    Text(
                        stringResource(R.string.linkedin),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                TextButton(onClick = {
                    safeOpenUri("https://tahaben.com.ly/blog/", context, clipboardManager)
                }) {
                    Text(stringResource(R.string.blog), color = MaterialTheme.colorScheme.tertiary)
                }
                TextButton(onClick = {
                    safeOpenUri("https://www.youtube.com/@tahabenly", context, clipboardManager)
                }) {
                    Text(
                        stringResource(R.string.youtube),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                TextButton(onClick = {
                    safeOpenUri("https://tahaben.com.ly/donations/", context, clipboardManager)
                }) {
                    Text(
                        stringResource(R.string.donate),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Text(
                text = stringResource(R.string.source_code),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.clickable {
                    safeOpenUri("https://github.com/tahaak67/Farhan", context, clipboardManager)
                },
                text = "https://github.com/tahaak67/Farhan",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = stringResource(R.string.version),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$versionName ($versionCode)",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                modifier = Modifier.clickable {
                    safeOpenUri(
                        "https://tahaben.com.ly/farhan-app-privacy-policy/",
                        context,
                        clipboardManager
                    )
                },
                text = stringResource(R.string.privacy_policy),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }

}

@Preview
@Composable
private fun AboutScreenPreview() {
    FarhanTheme(isSystemInDarkTheme(), colorStyle = ThemeColors.Classic) {
        AboutScreen(
            onNavigateUp = {},
            versionName = "1.0.0",
            versionCode = 1
        )
    }
}


fun safeOpenUri(link: String, context: Context, clipboardManager: ClipboardManager) {
    val url = Intent(Intent.ACTION_VIEW).apply {
        data = link.toUri()
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
        context.startActivity(url)
    } catch (ex: Exception) {
        clipboardManager.setText(AnnotatedString(link))
        Toast.makeText(
            context,
            R.string.cant_open_link_error_link_copied,
            Toast.LENGTH_LONG
        ).show()
    }
}