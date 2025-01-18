package com.suit.noteice.features.auth.domain

import com.suit.noteice.features.auth.data.AuthRequest
import com.suit.noteice.setup.DispatcherRule
import com.suit.noteice.setup.auth.mockTokensManager
import com.suit.noteice.setup.ktor.mockKtorEngine
import com.suit.noteice.utils.ktor.InputFieldError
import com.suit.noteice.utils.ktor.InputFieldException
import com.suit.noteice.utils.notes.TokensManager
import com.suit.noteice.utils.notes.data.TokenData
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.mockk.coVerify
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthClientTests {
    private lateinit var authClient: AuthClient
    private lateinit var tokensManager: TokensManager
    private val dispatcher = StandardTestDispatcher()
    private val defaultTokenData = TokenData("", "")
    private val inputFieldErrorEncoder = Json {ignoreUnknownKeys = true}

    @get: Rule
    val dispatcherRule = DispatcherRule(dispatcher)

    private inline fun <reified T> setup(
        response: T,
        statusCode: HttpStatusCode,
        locale: Locale = Locale.US,
        tokenData: TokenData = defaultTokenData
    ) {
        tokensManager = mockTokensManager(tokenData = tokenData)
        authClient = AuthClient(
            engine = mockKtorEngine(
                content = response,
                status = statusCode
            ),
            locale = locale,
            tokensManager = tokensManager
        )
    }

    @Test
    fun signIn_invalidInput_badRequest() = runTest {
        val inputFieldError = InputFieldError(login = "invalid format", password = "password too short")
        setup(
            response = inputFieldError,
            statusCode = HttpStatusCode.BadRequest
        )
        val exception = assertFailsWith<InputFieldException> { authClient.signIn(AuthRequest("", "")) }
        assertEquals(inputFieldErrorEncoder.encodeToString(inputFieldError), exception.message)
        coVerify(inverse = true) { tokensManager.saveTokenData(any()) }
    }
    @Test
    fun signUp_userAlreadyExists_conflict() = runTest {
        setup(
            response = Unit,
            statusCode = HttpStatusCode.Conflict
        )
        assertFailsWith<UserAlreadyExistsException> { authClient.signUp(AuthRequest("", "")) }
        coVerify(inverse = true) { tokensManager.saveTokenData(any()) }
    }
    @Test
    fun signIn_wrongCredentials_signInException() = runTest {
        setup(
            response = Unit,
            statusCode = HttpStatusCode.Unauthorized
        )
        assertFailsWith<SignInException> { authClient.signIn(AuthRequest("", "")) }
        coVerify(inverse = true) { tokensManager.saveTokenData(any()) }
    }
    @Test
    fun signIn_unknownError() = runTest {
        setup(
            response = Unit,
            statusCode = HttpStatusCode.Forbidden
        )
        assertFailsWith<ClientRequestException> { authClient.signIn(AuthRequest("", "")) }
        coVerify(inverse = true) { tokensManager.saveTokenData(any()) }
    }
    @Test
    fun signIn_success_tokensSaved() = runTest {
        setup(
            response = defaultTokenData,
            statusCode = HttpStatusCode.OK
        )
        authClient.signIn(AuthRequest("", ""))
        coVerify(exactly = 1) { tokensManager.saveTokenData(defaultTokenData) }
    }
}