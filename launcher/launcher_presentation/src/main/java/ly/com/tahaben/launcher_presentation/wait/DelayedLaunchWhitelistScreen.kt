package ly.com.tahaben.launcher_presentation.wait

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.components.AppExceptionListItem
import ly.com.tahaben.core_ui.components.SearchTextField
import ly.com.tahaben.core_ui.theme.FarhanTheme
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelayedLaunchWhiteListScreen(
    onNavigateUp: () -> Unit,
    onEvent: (DelayedLaunchEvent) -> Unit,
    state: DelayedLaunchWhiteListState
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var mDisplayMenu by remember { mutableStateOf(false) }
    var displaySearchField by remember { mutableStateOf(false) }
    var isHintVisible by remember { mutableStateOf(true) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = !displaySearchField,
                        enter = fadeIn(animationSpec = tween(1000)),
                        exit = fadeOut(animationSpec = tween(1))
                    ) {
                        Text(text = stringResource(id = R.string.white_lited_apps))
                    }
                },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    AnimatedVisibility(
                        visible = !displaySearchField,
                        enter = fadeIn(animationSpec = tween(1000)),
                        exit = fadeOut(animationSpec = tween(1))
                    )  {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                modifier = Modifier,
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        displaySearchField = !displaySearchField
                        if (!displaySearchField) {
                           // viewModel.onEvent(SearchEvent.HideSearch)
                        }
                    }) {
                        if (displaySearchField) {
                            Icon(Icons.Default.Close, stringResource(id = R.string.close_search))
                        } else {
                            Icon(Icons.Default.Search, stringResource(id = R.string.open_search_field))
                        }
                    }

                    AnimatedVisibility(visible = displaySearchField) {
                        SearchTextField(
                            text = state.searchQuery,
                            onValueChange = {
                                onEvent(DelayedLaunchEvent.OnSearchQueryChange(it))
                            },
                            shouldShowHint = isHintVisible,
                            onSearch = {
                                keyboardController?.hide()
                                onEvent(DelayedLaunchEvent.OnSearch)
                            },
                            onFocusChanged = {
                                if (it.isFocused){
                                    isHintVisible = false
                                    displaySearchField = true
                                }else{
                                    isHintVisible = true
                                    displaySearchField = false
                                }
                            }
                        )
                    }

                    // Creating Icon button for dropdown menu
                    IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
                        Icon(Icons.Default.MoreVert, "")
                    }

                    // Creating a dropdown menu
                    DropdownMenu(
                        expanded = mDisplayMenu,
                        onDismissRequest = { mDisplayMenu = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            onEvent(
                                DelayedLaunchEvent.OnShowSystemAppsChange(
                                    !state.isShowSystemApps
                                )
                            )
                        }, text = {
                            Text(
                                text = stringResource(R.string.show_system_apps),
                                textAlign = TextAlign.Center
                            )
                        },
                            trailingIcon = {
                                Checkbox(
                                    checked = state.isShowSystemApps,
                                    onCheckedChange = { checked ->
                                        onEvent(
                                            DelayedLaunchEvent.OnShowSystemAppsChange(
                                                checked
                                            )
                                        )
                                    },
                                    //colors = CheckboxDefaults.colors(MaterialTheme.colorScheme.primary)
                                )
                            })
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.show_whitelist_only),
                                    textAlign = TextAlign.Center
                                )
                            },
                            onClick = {
                                onEvent(
                                    DelayedLaunchEvent.OnShowWhiteListOnlyChange(
                                        !state.isShowWhiteListOnly
                                    )
                                )
                            },
                            trailingIcon = {
                                Checkbox(
                                    checked = state.isShowWhiteListOnly,
                                    onCheckedChange = { checked ->
                                        onEvent(
                                            DelayedLaunchEvent.OnShowWhiteListOnlyChange(
                                                checked
                                            )
                                        )
                                    },
//                                colors = CheckboxDefaults.colors(DarkYellow)
                                )
                            })
                    }
                }
            )
        }
    ) { paddingValues ->
        Crossfade(targetState = state.isLoading, label = "search results crossfade") {

            if (it) {
                Box(
                    modifier =
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmall),
                ) {
                    items(state.searchResults) { app ->
                        Timber.d("app: $app")
                        AppExceptionListItem(
                            app = app,
                            onClick = { isChecked ->
                                Timber.d("switched $isChecked")
                                if (isChecked) {
                                    onEvent(DelayedLaunchEvent.OnAddToWhiteList(app.packageName))
                                } else {
                                    onEvent(DelayedLaunchEvent.OnRemoveFromWhiteList(app.packageName))
                                }
                            }
                        )
                    }
                }
            }
        }

    }
}

@Preview
@Preview("arabic", locale = "ar")
@PreviewLightDark
@Composable
private fun DelayedLaunchScreenPreview() {
    FarhanTheme(isSystemInDarkTheme(), ThemeColors.Classic) {
        DelayedLaunchWhiteListScreen(onNavigateUp = {}, onEvent = {}, state = DelayedLaunchWhiteListState())
    }
}