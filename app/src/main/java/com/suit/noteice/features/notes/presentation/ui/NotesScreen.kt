package com.suit.noteice.features.notes.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.suit.noteice.features.notes.data.Note
import com.suit.noteice.features.notes.presentation.NotesIntent
import com.suit.noteice.features.notes.presentation.NotesUIState
import com.suit.noteice.features.notes.presentation.NotesViewModel
import com.suit.noteice.features.notes.presentation.ui.components.DeletionConfirmationDialog
import com.suit.noteice.features.notes.presentation.ui.components.EditNoteSheet
import com.suit.noteice.features.notes.presentation.ui.components.LoadingScreen
import com.suit.noteice.features.notes.presentation.ui.components.NotesColumn
import com.suit.noteice.features.notes.presentation.ui.components.SelectNotesBar
import com.suit.noteice.ui.theme.NoteIceTheme
import com.suit.noteice.utils.CustomResult
import com.suit.noteice.utils.isInProgress
import com.suit.noteice.utils.ui.LocalSnackbarProvider
import com.suit.noteice.utils.ui.NotesUIEvent
import com.suit.noteice.utils.ui.commonComponents.ConstrainedColumn
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
   if (notesFetchResult is CustomResult.Success) NotesScreenContent(
       uiState = uiState,
       onIntent = viewModel::handleIntent
   )
   else LoadingScreen(
       notesFetchFailed = notesFetchResult is CustomResult.Error,
       onRetry = { viewModel.handleIntent(NotesIntent.RefetchNotes) }
   )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreenContent(
    uiState: NotesUIState,
    onIntent: (NotesIntent) -> Unit
) {
    val openedNote = uiState.openedNote
    val selectedNotes = uiState.selectedNotes
    val isDeletionInProgress = uiState.notesDeletionResult.isInProgress()
    val selectAll by remember(uiState.notes, uiState.selectedNotes) {
        mutableStateOf(uiState.notes.size == uiState.selectedNotes.size)
    }
    var showDeletionConfirmation by rememberSaveable {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { onIntent(NotesIntent.OpenNote(0L)) }) {
                val addIcon = Icons.Filled.Add
                Icon(imageVector = addIcon, contentDescription = addIcon.name)
            }
        }
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            AnimatedVisibility(selectedNotes.isNotEmpty()) {
                SelectNotesBar(
                    selectAll = selectAll,
                    onSelectAll = { onIntent(NotesIntent.SelectAllNotes(it)) },
                    onDelete = { showDeletionConfirmation = true })
            }
            ConstrainedColumn(
                isScrollable = false) {
                PullToRefreshBox(
                    isRefreshing = uiState.notesFetchResult.isInProgress(),
                    onRefresh = { onIntent(NotesIntent.RefetchNotes) }) {
                    NotesColumn(
                        notes = uiState.notes,
                        enabled = !isDeletionInProgress,
                        selectedNotes = uiState.selectedNotes,
                        onClick = { onIntent(NotesIntent.OpenNote(it)) },
                        onSelect = { onIntent(NotesIntent.SelectNote(it)) }
                    )
                }
            }
        }
    }
    if (openedNote != null) {
        EditNoteSheet(
            note = openedNote,
            lastModifiedAt = uiState.openedNoteLastModifiedTime,
            onValueChange = { onIntent(NotesIntent.PerformNoteInput(it)) },
            onDismiss = {
                onIntent(NotesIntent.SaveOpenedNote)
                onIntent(NotesIntent.OpenNote(null)) })

    }
    if (showDeletionConfirmation) {
        DeletionConfirmationDialog(
            onDismiss = { showDeletionConfirmation = false },
            onConfirm = {
                showDeletionConfirmation = false
                onIntent(NotesIntent.DeleteSelectedNotes)
            }
        )
    }
}

@Preview
@Composable
fun NotesScreenPreview() {
    NoteIceTheme {
        Surface {
            NotesScreenContent(
                uiState = NotesUIState(
                    notes = listOf(
                        Note(title = "title"),
                        Note(id = 1, title = "title 2")
                    ),
                    selectedNotes = listOf(
                        Note(title = "title")
                    )
                ),
                onIntent = {})
        }
    }
}