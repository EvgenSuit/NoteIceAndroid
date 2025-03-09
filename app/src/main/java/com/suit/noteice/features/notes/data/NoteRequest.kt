package com.suit.noteice.features.notes.data

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val title: String,
    val content: String
)
