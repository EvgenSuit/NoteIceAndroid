package com.suit.noteice.utils.ui

sealed class NotesUIEvent {
    data class ShowSnackbar(val text: UIText): NotesUIEvent()
    data object NavigateToAuth: NotesUIEvent()
}
sealed class AuthUIEvent {
    data class ShowSnackbar(val text: UIText): AuthUIEvent()
    data object NavigateToNotesScreen: AuthUIEvent()
}