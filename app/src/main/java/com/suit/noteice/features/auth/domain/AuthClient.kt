package com.suit.noteice.features.auth.domain

import com.suit.noteice.features.auth.data.AuthRequest
import com.suit.noteice.utils.ktor.InputFieldError
import com.suit.noteice.utils.ktor.InputFieldException
import com.suit.noteice.utils.ktor.KtorConstants
import com.suit.noteice.utils.notes.TokensManager
import com.suit.noteice.utils.notes.data.TokenData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale

class AuthClient(
    engine: HttpClientEngine,
    private val tokensManager: TokensManager,
    private val locale: Locale,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val inputFieldErrorEncoder = Json {ignoreUnknownKeys = true}
    private val httpClient = HttpClient(engine) {
        expectSuccess = false
        install(Logging)
        install(ContentNegotiation) {
            json()
        }
        HttpResponseValidator {
            validateResponse { response ->
                when (response.status.value) {
                    HttpStatusCode.BadRequest.value -> {
                        val message = response.body<InputFieldError>()
                        throw InputFieldException(inputFieldErrorEncoder.encodeToString(message))
                    }
                    HttpStatusCode.Conflict.value -> {
                        throw UserAlreadyExistsException()
                    }
                    HttpStatusCode.Unauthorized.value -> {
                        throw SignInException()
                    }
                    in 300..399 -> throw RedirectResponseException(response, "")
                    in 400..499 -> throw ClientRequestException(response, "")
                    in 500..599 -> throw ServerResponseException(response, "")
                }
            }
        }
        defaultRequest {
            url(KtorConstants.BASE_AUTH_URL)
        }
    }

    suspend fun signUp(authRequest: AuthRequest) = withContext(dispatcher) {
        httpClient.post("signup") {
            contentType(ContentType.Application.Json)
            headers {
                headersOf(HttpHeaders.AcceptLanguage, locale.language)
            }
            setBody(authRequest)
        }
    }
    suspend fun signIn(authRequest: AuthRequest) = withContext(dispatcher) {
        val tokenData = httpClient.post("signin") {
            contentType(ContentType.Application.Json)
            headers {
                headersOf(HttpHeaders.AcceptLanguage, locale.language)
            }
            setBody(authRequest)
        }.body<TokenData>()
        tokensManager.saveTokenData(tokenData)
    }
    suspend fun logOut() = withContext(dispatcher) {
        httpClient.post("logout")
        tokensManager.clearTokenData()
    }
}