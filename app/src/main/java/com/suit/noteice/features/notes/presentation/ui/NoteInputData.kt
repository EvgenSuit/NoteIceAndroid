package com.suit.noteice.features.notes.presentation.ui

sealed class NoteInputData(val value: String) {
    data class Title(val title: String): NoteInputData(title)
    data class Content(val content: String): NoteInputData(content)
}