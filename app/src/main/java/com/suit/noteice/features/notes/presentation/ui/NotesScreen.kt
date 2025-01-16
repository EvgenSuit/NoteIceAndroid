package com.suit.noteice.features.notes.presentation.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.suit.noteice.features.notes.presentation.NotesIntent
import com.suit.noteice.features.notes.presentation.NotesViewModel
import com.suit.noteice.features.notes.presentation.ui.components.LoadingScreen
import com.suit.noteice.utils.CustomResult
import com.suit.noteice.utils.ui.LocalSnackbarProvider
import com.suit.noteice.utils.ui.NotesUIEvent
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotesScreen(
    viewModel: NotesViewModel = koinViewModel(),
    onNavigateToAuth: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbar = LocalSnackbarProvider.current
    val notesFetchResult = uiState.notesFetchResult
    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is NotesUIEvent.ShowSnackbar -> snackbar.showSnackbar(event.text)
                is NotesUIEvent.NavigateToAuth -> onNavigateToAuth()
            }
        }
    }
   if (notesFetchResult is CustomResult.Success) NotesScreenContent()
   else LoadingScreen(
       notesFetchFailed = notesFetchResult is CustomResult.Error,
       onRetry = { viewModel.handleIntent(NotesIntent.RefetchNotes) }
   )
}

@Composable
fun NotesScreenContent() {
    Text(text = "Notes screen")
}