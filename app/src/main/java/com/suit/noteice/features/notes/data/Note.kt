package com.suit.noteice.features.notes.data

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Long,
    val title: String,
    val content: String
)
