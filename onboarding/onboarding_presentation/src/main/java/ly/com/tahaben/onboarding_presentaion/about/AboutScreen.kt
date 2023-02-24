package ly.com.tahaben.onboarding_presentaion.about

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.White
import ly.com.tahaben.core_ui.mirror

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
                .padding(horizontal = spacing.spaceMedium)
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
                modifier = Modifier.clickable {
                    val url = Intent(Intent.ACTION_VIEW).apply {
                        data =
                            Uri.parse("https://github.com/tahaak67/Farhan")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try {
                        context.startActivity(url)
                    } catch (ex: Exception) {
                        clipboardManager.setText(AnnotatedString("https://github.com/tahaak67/Farhan"))
                        Toast.makeText(
                            context,
                            R.string.cant_open_link_error_link_copied,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                text = "https://github.com/tahaak67/Farhan",
                style = MaterialTheme.typography.h4,
                color = Color.Blue
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Text(
                text = stringResource(R.string.version),
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Text(
                text = "$versionName ($versionCode)",
                style = MaterialTheme.typography.h4
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Text(
                modifier = Modifier.clickable {
                    val url = Intent(Intent.ACTION_VIEW).apply {
                        data =
                            Uri.parse("https://tahaben.com.ly/farhan-app-privacy-policy/")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try {
                        context.startActivity(url)
                    } catch (ex: Exception) {
                        clipboardManager.setText(AnnotatedString("https://tahaben.com.ly/farhan-app-privacy-policy/"))
                        Toast.makeText(
                            context,
                            R.string.cant_open_link_error_link_copied,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                text = stringResource(R.string.privacy_policy),
                style = MaterialTheme.typography.h4,
                color = Color.Blue
            )
        }
    }

}