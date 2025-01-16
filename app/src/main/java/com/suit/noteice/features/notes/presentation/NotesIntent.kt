package com.suit.noteice.features.notes.presentation

sealed class NotesIntent {
    data object RefetchNotes: NotesIntent()
}