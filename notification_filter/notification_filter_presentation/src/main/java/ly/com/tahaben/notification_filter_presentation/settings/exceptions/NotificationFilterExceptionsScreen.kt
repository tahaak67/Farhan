package ly.com.tahaben.notification_filter_presentation.settings.exceptions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.SearchEvent
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core_ui.DarkYellow
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.components.AppExceptionListItem
import ly.com.tahaben.core_ui.components.SearchTextField
import ly.com.tahaben.core_ui.mirror
import timber.log.Timber

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NotificationFilterExceptionsScreen(
    snackbarHostState: SnackbarHostState,
    onNavigateUp: () -> Unit,
    viewModel: NotificationExceptionsViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var mDisplayMenu by remember { mutableStateOf(false) }
    var displaySearchField by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = keyboardController) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                    keyboardController?.hide()
                }
                is UiEvent.NavigateUp -> onNavigateUp()
                else -> Unit
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                AnimatedVisibility(
                    visible = !displaySearchField,
                    enter = fadeIn(animationSpec = tween(1000)),
                    exit = fadeOut(animationSpec = tween(1))
                ) {
                    Text(text = stringResource(id = R.string.exceptions))
                }
            },
            navigationIcon = {
                AnimatedVisibility(
                    visible = !displaySearchField,
                    enter = fadeIn(animationSpec = tween(1000)),
                    exit = fadeOut(animationSpec = tween(1))
                ) {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            modifier = Modifier.mirror(),
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            },
            actions = {
                IconButton(onClick = {
                    displaySearchField = !displaySearchField
                    if (!displaySearchField) {
                        viewModel.onEvent(SearchEvent.HideSearch)
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
                        text = state.query,
                        onValueChange = {
                            viewModel.onEvent(SearchEvent.OnQueryChange(it))
                        },
                        shouldShowHint = state.isHintVisible,
                        onSearch = {
                            keyboardController?.hide()
                            viewModel.onEvent(SearchEvent.OnSearch)
                        },
                        onFocusChanged = {
                            viewModel.onEvent(SearchEvent.OnSearchFocusChange(it.isFocused))
                        }
                    )
                }

                IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
                    Icon(Icons.Default.MoreVert, stringResource(id = R.string.drop_down_menu))
                }

                DropdownMenu(
                    expanded = mDisplayMenu,
                    onDismissRequest = { mDisplayMenu = false }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(id = R.string.show_system_apps),
                                textAlign = TextAlign.Center
                            )
                        }, onClick = {
                            viewModel.onEvent(
                                SearchEvent.OnSystemAppsVisibilityChange(
                                    !state.showSystemApps
                                )
                            )
                        },
                        trailingIcon = {
                            Checkbox(
                                checked = state.showSystemApps,
                                onCheckedChange = { checked ->
                                    viewModel.onEvent(
                                        SearchEvent.OnSystemAppsVisibilityChange(
                                            checked
                                        )
                                    )
                                },
                                colors = CheckboxDefaults.colors(DarkYellow)
                            )
                        })
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.show_exceptions_only),
                                textAlign = TextAlign.Center
                            )
                        },
                        onClick = {
                            viewModel.onEvent(
                                SearchEvent.OnExceptionsOnlyChange(
                                    !state.showExceptionsOnly
                                )
                            )
                        },
                        trailingIcon = {
                            Checkbox(
                                checked = state.showExceptionsOnly,
                                onCheckedChange = { checked ->
                                    viewModel.onEvent(
                                        SearchEvent.OnExceptionsOnlyChange(
                                            checked
                                        )
                                    )
                                },
                                colors = CheckboxDefaults.colors(DarkYellow)
                            )
                        })
                }
            }
        )
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
                        .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmall),
                ) {
                    items(state.searchResults) { app ->
                        Timber.d("app: $app")
                        AppExceptionListItem(
                            app = app,
                            onClick = { isChecked ->
                                Timber.d("switched $isChecked")
                                if (isChecked) {
                                    viewModel.addAppToExceptions(app.packageName)

                                } else {
                                    viewModel.removeAppFromExceptions(app.packageName)
                                }
                            }
                        )
                    }
                }
            }
        }
    }


}