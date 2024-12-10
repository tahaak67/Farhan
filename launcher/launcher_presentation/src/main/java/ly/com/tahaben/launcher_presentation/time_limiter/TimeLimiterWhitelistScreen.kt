package ly.com.tahaben.launcher_presentation.time_limiter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
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
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.components.SearchTextField
import ly.com.tahaben.core_ui.mirror
import ly.com.tahaben.launcher_presentation.component.AppExceptionListItem
import timber.log.Timber

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 18,Feb,2023
 */

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimeLimiterWhitelistScreen(
    snackbarHostState: SnackbarHostState,
    onNavigateUp: () -> Unit,
    viewModel: TimeLimiterWhitelistViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var mDisplayMenu by remember { mutableStateOf(false) }
    var displaySearchField by remember { mutableStateOf(false) }
    val isShowSystemApps = remember { mutableStateOf(state.showSystemApps) }



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
                    Text(text = stringResource(id = R.string.white_lited_apps))
                }
            },
//            backgroundColor = White,
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

                    }, text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isShowSystemApps.value,
                                onCheckedChange = { checked ->
                                    isShowSystemApps.value = checked
                                    viewModel.onEvent(
                                        SearchEvent.OnSystemAppsVisibilityChange(
                                            checked
                                        )
                                    )
                                },
                                colors = CheckboxDefaults.colors(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = stringResource(R.string.show_system_apps),
                                textAlign = TextAlign.Center
                            )
                        }
                    })
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
                                    viewModel.addAppToWhiteList(app.packageName)

                                } else {
                                    viewModel.removeAppFromWhiteList(app.packageName)
                                }
                            }
                        )
                    }
                }
            }
        }
    }


}