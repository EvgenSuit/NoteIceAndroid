package com.suit.noteice.features.auth.presentation

sealed class NotesIntent {
    data object RefetchNotes: NotesIntent()
}