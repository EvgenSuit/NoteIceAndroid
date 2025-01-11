package com.suit.noteice.utils.notes

import com.suit.noteice.setup.DispatcherRule
import com.suit.noteice.setup.ktor.mockKtorEngine
import com.suit.noteice.setup.notes.NotesClientConstants
import com.suit.noteice.setup.notes.mockNotesClient
import com.suit.noteice.setup.notes.mockTokensManager
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.mockk.coVerify
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NotesClientTests {
    private lateinit var notesClient: NotesClient
    private val tokensManager = mockTokensManager()
    private val testDispatcher = StandardTestDispatcher()

    @get: Rule
    val dispatcherRule = DispatcherRule(testDispatcher)

    private fun setup(
        inputRefreshTokenEngine: MockEngine? = null,
        inputNotesEngine: MockEngine? = null,
        inputTokensManager: TokensManager = tokensManager
    ) {
        notesClient = mockNotesClient(
            inputRefreshTokenEngine = inputRefreshTokenEngine,
            inputNotesEngine = inputNotesEngine,
            inputTokensManager = inputTokensManager,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun getNotes_savedTokensNull_noRequestPerformed() = runTest {
        val tokensManager = mockTokensManager(null)
        setup(
            inputTokensManager = tokensManager
        )
        assertEquals(null, notesClient.getNotes())
        coVerify(exactly = 1) { tokensManager.getSavedTokenData() }
    }

    @Test
    fun getNotes_tokenRefreshFailed_throwsTokenRefreshFailed() = runTest {
        setup(
            inputRefreshTokenEngine = mockKtorEngine(
                content = NotesClientConstants.defaultTokenData,
                status = HttpStatusCode.Unauthorized
            ),
            inputNotesEngine = mockKtorEngine(
                content = NotesClientConstants.defaultNotes,
                status = HttpStatusCode.Unauthorized
            )
        )
        assertFailsWith<TokenRefreshFailed> { notesClient.getNotes() }
        coVerify(exactly = 2) { tokensManager.getSavedTokenData() }
    }

    @Test
    fun getNotes_tokenRefreshSucceeded_throwsClientRequestException() = runTest {
        setup(
            inputRefreshTokenEngine = mockKtorEngine(
                content = NotesClientConstants.defaultTokenData,
                status = HttpStatusCode.OK
            ),
            inputNotesEngine = mockKtorEngine(
                content = NotesClientConstants.defaultNotes,
                status = HttpStatusCode.Unauthorized
            )
        )
        assertFailsWith<ClientRequestException> { notesClient.getNotes() }
        coVerify(exactly = 2) { tokensManager.getSavedTokenData() }
        coVerify(exactly = 1) { tokensManager.saveTokenData(any()) }
    }

    @Test
    fun getNotes_success() = runTest {
        setup(
            inputNotesEngine = mockKtorEngine(
                content = NotesClientConstants.defaultNotes,
                status = HttpStatusCode.OK
            ),
            inputRefreshTokenEngine = mockKtorEngine(
                content = NotesClientConstants.defaultTokenData,
                status = HttpStatusCode.OK
            ),
            inputTokensManager = tokensManager
        )
        assertEquals(NotesClientConstants.defaultNotes, notesClient.getNotes())
        coVerify(exactly = 2) { tokensManager.getSavedTokenData() }
    }
}