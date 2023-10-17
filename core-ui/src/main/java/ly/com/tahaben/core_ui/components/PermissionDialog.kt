package ly.com.tahaben.core_ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.core_ui.LocalSpacing

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 13,May,2023
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Text(text = stringResource(id = R.string.permission_required))
            Spacer(modifier = Modifier.height(spacing.spaceSmall))
            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined
                ).asString(context = context)
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Divider()
            Text(
                text = if (isPermanentlyDeclined) {
                    stringResource(id = R.string.grant_runtime_permission)
                } else {
                    stringResource(id = R.string.ok)
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isPermanentlyDeclined) {
                            onGoToAppSettingsClick()
                        } else {
                            onOkClick()
                        }
                    }
                    .padding(spacing.spaceMedium)
            )
        }
    }
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): UiText
}

class PostNotificationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): UiText {
        return if (isPermanentlyDeclined) {
            UiText.StringResource(R.string.post_notification_permission_permanently_declined_description)
        } else {
            UiText.StringResource(R.string.post_notification_permission_description)
        }
    }
}

