package com.devpaul.core.designsystem.component.mvi

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.devpaul.core.platform.lifecycle.StatefulViewModel

@Composable
fun <UiState, UiIntent, UiEvent> BaseScreenWithState(
    viewModel: StatefulViewModel<UiState, UiIntent, UiEvent>,
    onUiEvent: suspend (event: UiEvent, showSnackbar: suspend (String) -> Unit) -> Unit =
        { _, _ -> },
    initialIntent: UiIntent? = null,
    content: @Composable (
        uiState: UiState,
        onIntent: (UiIntent) -> Unit,
    ) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentEventHandler by rememberUpdatedState(onUiEvent)

    LaunchedEffect(viewModel, initialIntent) {
        initialIntent?.let(viewModel::executeUiIntent)
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvents.collect { event ->
            currentEventHandler(event) { message ->
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        content(
            uiState,
            viewModel::executeUiIntent,
        )
    }
}
