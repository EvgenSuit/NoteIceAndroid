package com.suit.noteice.features.notes.viewModel

import app.cash.turbine.test
import com.suit.noteice.features.notes.NotesBaseRule
import com.suit.noteice.setup.DispatcherRule
import com.suit.noteice.setup.ktor.mockKtorEngine
import com.suit.noteice.setup.auth.mockTokensManager
import com.suit.noteice.utils.CustomResult
import com.suit.noteice.utils.notes.data.TokenData
import com.suit.noteice.utils.ui.NotesUIEvent
import io.ktor.http.HttpStatusCode
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotesViewModelTests {
    @get: Rule(order = 1)
    val notesBaseRule = NotesBaseRule()
    @get: Rule(order = 0)
    val dispatcherRule = DispatcherRule(notesBaseRule.testDispatcher)

    @Test
    fun fetchNotes_noTokensSaved_navigateToAuthEventEmitted() = runTest {
        notesBaseRule.apply {
            setupNotesClient(
                tokensManager = mockTokensManager(tokenData = TokenData())
            )
            setupNotesRepository()
            setupViewModel()

            viewModel.uiState.test {
                skipItems(1)
                startUIEventCollection()

                assertEquals(CustomResult.InProgress, awaitItem().notesFetchResult)
                assertEquals(CustomResult.None, awaitItem().notesFetchResult)
                stopUIEventCollection()

                assertEquals(listOf(NotesUIEvent.NavigateToAuth), collectedUIEvents.toList())
            }
            coVerify(exactly = 1) { notesRepository.fetchNotes() }
        }
    }
    @Test
    fun fetchNotes_refreshTokenFailed_navigateToAuthEventEmitted() = runTest {
        notesBaseRule.apply {
            setupNotesClient(
                refreshTokenEngine = mockKtorEngine(
                    content = "",
                    status = HttpStatusCode.Unauthorized
                ),
                notesEngine = mockKtorEngine(
                    content = "",
                    status = HttpStatusCode.Unauthorized
                )
            )
            setupNotesRepository()
            setupViewModel()
            viewModel.uiState.test {
                skipItems(1)
                startUIEventCollection()

                assertEquals(CustomResult.InProgress, awaitItem().notesFetchResult)
                assertEquals(CustomResult.None, awaitItem().notesFetchResult)
                stopUIEventCollection()

                assertEquals(listOf(NotesUIEvent.NavigateToAuth), collectedUIEvents.toList())
            }
            coVerify(exactly = 1) { notesRepository.fetchNotes() }
        }
    }
    @Test
    fun fetchNotes_fetchFailed_noUIEventsEmitted() = runTest {
        notesBaseRule.apply {
            setupNotesClient(
                notesEngine = mockKtorEngine(
                    content = "",
                    status = HttpStatusCode.Unauthorized
                )
            )
            setupNotesRepository()
            setupViewModel()
            viewModel.uiState.test {
                skipItems(1)
                startUIEventCollection()

                assertEquals(CustomResult.InProgress, awaitItem().notesFetchResult)
                assertEquals(CustomResult.Error, awaitItem().notesFetchResult)
                stopUIEventCollection()

                assertTrue(collectedUIEvents.isEmpty())
            }
            coVerify(exactly = 1) { notesRepository.fetchNotes() }
        }
    }
}