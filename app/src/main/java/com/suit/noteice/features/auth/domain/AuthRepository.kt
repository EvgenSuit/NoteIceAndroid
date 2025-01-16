package com.suit.noteice.features.auth.domain

import com.suit.noteice.features.auth.data.AuthRequest

class AuthRepository(
    private val authClient: AuthClient
) {
    suspend fun signUp(email: String, password: String)
    = authClient.signUp(AuthRequest(email, password))

    suspend fun signIn(email: String, password: String)
    = authClient.signIn(AuthRequest(email, password))

    suspend fun logOut() = authClient.logOut()
}