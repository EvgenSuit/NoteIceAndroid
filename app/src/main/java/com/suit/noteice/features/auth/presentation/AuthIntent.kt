package com.suit.noteice.features.auth.presentation

sealed class AuthIntent {
    data object ChangeAuthType: AuthIntent()
    data class Authenticate(val email: String, val password: String): AuthIntent()
}