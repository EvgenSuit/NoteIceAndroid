package com.suit.noteice.utils.notes

import com.suit.noteice.features.notes.data.Note
import com.suit.noteice.utils.ktor.KtorConstants
import com.suit.noteice.utils.notes.data.RefreshTokenRequest
import com.suit.noteice.utils.notes.data.TokenData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class NotesClient(
    refreshTokenEngine: HttpClientEngine,
    notesEngine: HttpClientEngine,
    private val tokensManager: TokensManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val refreshTokenHttpClient = HttpClient(refreshTokenEngine) {
        expectSuccess = true
        install(Logging)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
            })
        }
        defaultRequest {
            url("${KtorConstants.BASE_AUTH_URL}refresh")
        }
    }

    private val notesHttpClient = HttpClient(notesEngine) {
        expectSuccess = true
        install(Logging)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
            })
        }
        defaultRequest {
            url(KtorConstants.BASE_NOTES_URL)
        }
    }

    init {
        notesHttpClient.plugin(HttpSend).intercept { request ->
            val tokens = tokensManager.getSavedTokenData()

            request.headers {
                append("Authorization", "Bearer ${tokens.accessToken!!}")
            }
            val originalCall = execute(request)
            if (originalCall.response.status.value == 401) {
                try {
                    val newTokens = refreshTokenHttpClient.post("") {
                        contentType(ContentType.Application.Json)
                        setBody(RefreshTokenRequest(tokens.refreshToken!!))
                    }.body<TokenData>()
                    tokensManager.saveTokenData(newTokens)
                    request.headers {
                        remove("Authorization")
                        append("Authorization", "Bearer ${newTokens.accessToken!!}")
                    }
                } catch (e: Exception) {
                    // clear tokens so that no network requests will be performed the next time a user enters the app while having invalid tokens
                    tokensManager.clearTokenData()
                    throw TokenRefreshFailed("Token refresh failed: ${e.message}")
                }
                execute(request)
            } else originalCall
        }
    }

    suspend fun getNotes(): List<Note>? = withContext(dispatcher) {
        if (tokensManager.getSavedTokenData() == TokenData()) null
        else notesHttpClient.get("").body<List<Note>>()
    }
}