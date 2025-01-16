package com.suit.noteice.features.notes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suit.noteice.features.notes.data.Note
import com.suit.noteice.features.notes.domain.NotesRepository
import com.suit.noteice.utils.CustomResult
import com.suit.noteice.utils.notes.TokenRefreshFailed
import com.suit.noteice.utils.ui.NotesUIEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel(
    private val notesRepository: NotesRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(NotesUIState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<NotesUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        fetchNotes()
    }

    fun handleIntent(intent: NotesIntent) {
        when (intent) {
            is NotesIntent.RefetchNotes -> fetchNotes()
        }
    }

    private fun fetchNotes() {
        viewModelScope.launch {
            updateNotesFetchResult(CustomResult.InProgress)
            try {
                val notes = notesRepository.fetchNotes()
                if (notes == null) {
                    _uiEvent.emit(NotesUIEvent.NavigateToAuth)
                    updateNotesFetchResult(CustomResult.None)
                    return@launch
                }
                _uiState.update { it.copy(notes) }
                updateNotesFetchResult(CustomResult.Success)
            } catch (e: Exception) {
                println(e)
                if (e is TokenRefreshFailed) {
                    _uiEvent.emit(NotesUIEvent.NavigateToAuth)
                    updateNotesFetchResult(CustomResult.None)
                    return@launch
                }
                updateNotesFetchResult(CustomResult.Error)
            }
        }
    }

    private fun updateNotesFetchResult(result: CustomResult) =
        _uiState.update { it.copy(notesFetchResult = result) }
}

data class NotesUIState(
    val notes: List<Note> = emptyList(),
    val notesFetchResult: CustomResult = CustomResult.None
)