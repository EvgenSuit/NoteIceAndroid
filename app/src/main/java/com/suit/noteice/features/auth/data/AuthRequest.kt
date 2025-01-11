package com.suit.noteice.features.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val login: String,
    val password: String
)
