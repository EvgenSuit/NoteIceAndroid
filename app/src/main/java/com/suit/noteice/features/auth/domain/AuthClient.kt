package com.suit.noteice.features.auth.domain

import com.suit.noteice.features.auth.data.AuthRequest
import com.suit.noteice.utils.KtorConstants
import com.suit.noteice.utils.notes.data.TokenData
import com.suit.noteice.utils.notes.TokensManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AuthClient(
    engine: HttpClientEngine,
    private val tokensManager: TokensManager
) {
    private val httpClient = HttpClient(engine) {
        expectSuccess = true
        install(Logging)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
            })
        }
        defaultRequest {
            url(KtorConstants.BASE_AUTH_URL)
        }
    }

    suspend fun signUp(authRequest: AuthRequest) {
        val tokenData = httpClient.post("signup") {
            contentType(ContentType.Application.Json)
            setBody(authRequest)
        }.body<TokenData>()
        println("saving tokens: $tokenData")
        tokensManager.saveTokenData(tokenData)
    }
    suspend fun signIn(authRequest: AuthRequest) {
        val tokenData = httpClient.post("signin") {
            contentType(ContentType.Application.Json)
            setBody(authRequest)
        }.body<TokenData>()
        println("saving tokens: $tokenData")
        tokensManager.saveTokenData(tokenData)
    }
}