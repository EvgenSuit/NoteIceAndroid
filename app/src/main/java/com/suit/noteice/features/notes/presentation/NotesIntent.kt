package com.suit.noteice.features.notes.presentation

import com.suit.noteice.features.notes.data.Note
import com.suit.noteice.features.notes.presentation.ui.NoteInputData

sealed class NotesIntent {
    data object RefetchNotes: NotesIntent()
    data class OpenNote(val id: Long?): NotesIntent()
    data class SelectNote(val id: Long): NotesIntent()
    data class SelectAllNotes(val select: Boolean): NotesIntent()
    data class PerformNoteInput(val noteInputData: NoteInputData): NotesIntent()
    data object SaveOpenedNote: NotesIntent()
    data object DeleteSelectedNotes: NotesIntent()
}