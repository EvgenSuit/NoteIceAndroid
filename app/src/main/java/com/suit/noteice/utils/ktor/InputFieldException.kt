package com.suit.noteice.utils.ktor

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import java.lang.Exception

@Keep
@Serializable
data class InputFieldError(
    val login: String? = null,
    val password: String? = null
)

class InputFieldException(message: String): Exception(message)