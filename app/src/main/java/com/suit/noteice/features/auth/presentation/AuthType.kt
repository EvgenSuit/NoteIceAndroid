package com.suit.noteice.features.auth.presentation

sealed class AuthType {
    data object SignIn: AuthType()
    data object SignUp: AuthType()
}