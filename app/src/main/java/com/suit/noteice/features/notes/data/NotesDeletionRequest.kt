package com.suit.noteice.features.notes.data

import kotlinx.serialization.Serializable

@Serializable
data class NotesDeletionRequest(
    val ids: List<Long>
)
