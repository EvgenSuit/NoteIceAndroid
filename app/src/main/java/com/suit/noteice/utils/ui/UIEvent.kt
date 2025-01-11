package com.suit.noteice.utils.ui

sealed class NotesUIEvent {
    data class ShowSnackbar(val text: UIText): NotesUIEvent()
    data object NavigateToAuth: NotesUIEvent()
}