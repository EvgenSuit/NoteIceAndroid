package com.suit.noteice.features.auth.domain

import com.suit.noteice.features.auth.data.AuthRequest
import com.suit.noteice.utils.ktor.InputFieldException
import com.suit.noteice.utils.ktor.KtorConstants
import com.suit.noteice.utils.ktor.InputFieldError
import com.suit.noteice.utils.notes.data.TokenData
import com.suit.noteice.utils.notes.TokensManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale

class AuthClient(
    engine: HttpClientEngine,
    private val tokensManager: TokensManager,
    private val locale: Locale
) {
    private val httpClient = HttpClient(engine) {
        install(Logging)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
            })
        }
        HttpResponseValidator {
            validateResponse { response ->
                when (response.status) {
                    HttpStatusCode.BadRequest -> {
                        val message = response.body<InputFieldError>()
                        throw InputFieldException(Json{ignoreUnknownKeys = true}.encodeToString(message))
                    }
                    HttpStatusCode.Conflict -> {
                        throw UserAlreadyExistsException()
                    }
                    HttpStatusCode.Unauthorized -> {
                        throw SignInException()
                    }
                }
            }
        }
        defaultRequest {
            url(KtorConstants.BASE_AUTH_URL)
        }
    }

    suspend fun signUp(authRequest: AuthRequest) {
        httpClient.post("signup") {
            contentType(ContentType.Application.Json)
            headers {
                headersOf(HttpHeaders.AcceptLanguage, locale.language)
            }
            setBody(authRequest)
        }
    }
    suspend fun signIn(authRequest: AuthRequest) {
        val tokenData = httpClient.post("signin") {
            contentType(ContentType.Application.Json)
            headers {
                headersOf(HttpHeaders.AcceptLanguage, locale.language)
            }
            setBody(authRequest)
        }.body<TokenData>()
        println("Received tokens: $tokenData")
        tokensManager.saveTokenData(tokenData)
    }
    suspend fun logOut() {
        httpClient.post("logout")
        tokensManager.clearTokenData()
    }
}