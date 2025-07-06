package ly.com.tahaben.farhan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core_ui.theme.FarhanTheme
import java.io.File


class CrashDetailsActivity : ComponentActivity() {
    companion object {
        private const val EXTRA_FILE_PATH = "file_path"

        fun createIntent(context: Context, filePath: String) =
            Intent(context, CrashDetailsActivity::class.java).apply {
                putExtra(EXTRA_FILE_PATH, filePath)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val crashLog = readCrashLog()
        setContent {
            FarhanTheme(darkMode = true, colorStyle = ThemeColors.Classic) {
                Scaffold(Modifier.fillMaxSize()) {
                    CrashDetailsScreen(
                        modifier = Modifier.padding(it),
                        crashLog = crashLog,
                        context = this,
                        finishActivity = ::finish
                    )
                }
            }
        }
    }

    private fun readCrashLog(): String {
        return intent.getStringExtra(EXTRA_FILE_PATH)
            ?.let { File(it).readText() }
            ?: "No crash data available"
    }
}

@Composable
fun CrashDetailsScreen(
    modifier: Modifier,
    crashLog: String,
    context: Context,
    finishActivity: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var isCheckIssueExistChecked by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Crash Report",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            stringResource(R.string.confirm_issue_not_exist_msg),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            stringResource(R.string.ignore_this_screen_unkown),
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = crashLog,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            readOnly = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
        )
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.create_github_issue))
        }
        Button(
            onClick = {
                clipboardManager.setText(AnnotatedString(crashLog))
                Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
                      },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.copy_to_clipboard))
        }
        Button(
            onClick = { finishActivity() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.close))
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.open_github)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.open_github_msg))
                        Row {
                            Checkbox(
                                checked = isCheckIssueExistChecked,
                                onCheckedChange = { isCheckIssueExistChecked = it })
                            Text(stringResource(R.string.verify_no_existing_issue_checkbox_message))
                        }

                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openGitHubIssue(crashLog, context)
                            showDialog = false
                            finishActivity()
                        },
                        enabled = isCheckIssueExistChecked
                    ) {
                        Text(stringResource(R.string.continuee))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

private fun openGitHubIssue(crashLog: String, context: Context) {

    val crashSignature = extractCrashSignature(crashLog)

    val issueBody = """
**⚠️ IMPORTANT: Please search existing issues before creating a new one**
Tip: to check the boxes below put an 'x' between the brackets (e.g. [x])
        - [ ] I have searched for similar crashes and confirmed this is not a duplicate
        - [ ] This crash is reproducible
        - [ ] I can provide steps to reproduce (if applicable)
       
**Steps to Reproduce (if known):**
        1. 
        2. 
        3. 
        
**Additional Context:**
        Write any other context about the crash here.

```
   $crashLog
```

    """

    val url = "https://github.com/tahaak67/Farhan/issues/new" +
            "?title=${Uri.encode("Crash: $crashSignature")}" +
            "&body=${Uri.encode(issueBody)}" +
            "&labels=${Uri.encode("crash")}"



    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = url.toUri()
    }
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FarhanTheme(darkMode = true, colorStyle = ThemeColors.Classic) {
        Scaffold(Modifier.fillMaxSize()) {
            CrashDetailsScreen(
                Modifier.padding(it),
                LoremIpsum(300).values.joinToString(),
                LocalContext.current,
                {}
            )
        }
    }
}

private fun extractCrashSignature(crashLog: String): String {
    // Extract the key parts of the crash (exception type, method name, line number)
    val lines = crashLog.split('\n')
    val exceptionLine = lines.find { it.contains("Exception") || it.contains("Error") }
    val locationLine = lines.find { it.contains("at ly.com.tahaben") }

    return listOfNotNull(
        exceptionLine?.substringBefore(':')?.trim(),
        locationLine?.substringAfter("at ")?.substringBefore('(')?.trim()
    ).joinToString(" ")
}
