package com.suit.noteice.features.notes.domain

import com.suit.noteice.utils.notes.NotesClient

class NotesRepository(
    private val notesClient: NotesClient
) {
    suspend fun fetchNotes() = notesClient.getNotes()
}