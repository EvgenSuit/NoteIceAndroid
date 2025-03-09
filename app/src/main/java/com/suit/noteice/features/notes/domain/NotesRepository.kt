package com.suit.noteice.features.notes.domain

import com.suit.noteice.features.notes.data.Note
import com.suit.noteice.features.notes.data.NoteRequest
import com.suit.noteice.features.notes.data.NotesDeletionRequest
import com.suit.noteice.utils.notes.NotesClient

class NotesRepository(
    private val notesClient: NotesClient
) {
    suspend fun deleteNotes(notes: List<Note>) =
        notesClient.deleteNotes(NotesDeletionRequest(notes.map { it.id }))

    suspend fun editNote(note: Note) =
        notesClient.editNote(note.id, NoteRequest(title = note.title, content = note.content))

    suspend fun saveNote(note: Note) =
        notesClient.saveNote(NoteRequest(title = note.title, content = note.content))

    suspend fun fetchNotes() = notesClient.getNotes()
}