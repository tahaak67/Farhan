package ly.com.tahaben.infinite_scroll_blocker_presentation.exceptions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
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
import ly.com.tahaben.core.util.UiEvent.NavigateUp
import ly.com.tahaben.core.util.UiEvent.ShowSnackbar
import ly.com.tahaben.core_ui.DarkYellow
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.White
import ly.com.tahaben.core_ui.components.SearchTextField
import ly.com.tahaben.core_ui.mirror
import ly.com.tahaben.infinite_scroll_blocker_presentation.components.AppExceptionListItem
import timber.log.Timber


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InfiniteScrollExceptionsScreen(
    scaffoldState: ScaffoldState,
    onNavigateUp: () -> Unit,
    viewModel: InfiniteScrollingBlockerExceptionsViewModel = hiltViewModel()
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
                is ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                    keyboardController?.hide()
                }
                is NavigateUp -> onNavigateUp()
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
                    DropdownMenuItem(onClick = {
                        viewModel.onEvent(
                            SearchEvent.OnSystemAppsVisibilityChange(
                                !state.showSystemApps
                            )
                        )
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                            Text(
                                text = stringResource(R.string.show_system_apps),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    DropdownMenuItem(onClick = {
                        viewModel.onEvent(
                            SearchEvent.OnExceptionsOnlyChange(
                                !state.showExceptionsOnly
                            )
                        )
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                            Text(
                                text = stringResource(R.string.show_exceptions_only),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        )
        Crossfade(targetState = state.isLoading) {

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
                    //Timber.d("app: list  $list")
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