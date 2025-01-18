package com.suit.noteice.setup.notes

import com.suit.noteice.features.notes.data.Note
import com.suit.noteice.setup.auth.mockTokensManager
import com.suit.noteice.setup.ktor.mockKtorEngine
import com.suit.noteice.utils.notes.NotesClient
import com.suit.noteice.utils.notes.TokensManager
import com.suit.noteice.utils.notes.data.TokenData
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import io.mockk.spyk
import kotlinx.coroutines.test.TestDispatcher

fun mockNotesClient(
    inputRefreshTokenEngine: MockEngine? = null,
    inputNotesEngine: MockEngine? = null,
    inputTokensManager: TokensManager? = null,
    dispatcher: TestDispatcher,
): NotesClient {
    val refreshTokenEngine = inputRefreshTokenEngine ?: mockKtorEngine(
        content = NotesClientConstants.defaultTokenData,
        status = HttpStatusCode.OK
    )
    val notesEngine = inputNotesEngine ?: mockKtorEngine(
        content = NotesClientConstants.defaultNotes,
        status = HttpStatusCode.OK
    )
    return spyk(
        NotesClient(
            refreshTokenEngine = refreshTokenEngine,
            notesEngine = notesEngine,
            tokensManager = inputTokensManager ?: mockTokensManager(),
            dispatcher = dispatcher
        )
    )
}

object NotesClientConstants {
    val defaultNotes = listOf(Note(id = 0, title = "title", content = "content"))
    val defaultTokenData = TokenData("", "")
}