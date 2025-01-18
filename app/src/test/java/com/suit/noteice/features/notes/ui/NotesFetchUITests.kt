package com.suit.noteice.features.notes.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.suit.noteice.R
import com.suit.noteice.features.notes.NotesBaseRule
import com.suit.noteice.features.notes.presentation.ui.NotesScreen
import com.suit.noteice.setup.DispatcherRule
import com.suit.noteice.setup.ktor.mockKtorEngine
import com.suit.noteice.setup.notes.NotesClientConstants
import com.suit.noteice.setup.auth.mockTokensManager
import com.suit.noteice.utils.notes.data.TokenData
import com.suit.noteice.utils.ui.getString
import com.suit.noteice.utils.ui.setCustomContent
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class NotesFetchUITests {
    @get: Rule(order = 1)
    val notesBaseRule = NotesBaseRule()
    @get: Rule(order = 0)
    val dispatcherRule = DispatcherRule(notesBaseRule.testDispatcher)
    @get: Rule
    val composeRule = createComposeRule()

    @Test
    fun fetchNotes_tokenRefreshFailed_navigatedToAuth() = runTest {
        var didNavigateToAuth = false
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
            composeRule.setCustomContent(uiContent = {
                NotesScreen(viewModel = viewModel,
                    onNavigateToAuth = { didNavigateToAuth = true })
            }) {
                advance()
                onNodeWithText(getString(R.string.could_not_fetch_notes)).assertDoesNotExist()
                assertTrue(didNavigateToAuth)
            }
        }
    }

    @Test
    fun fetchNotes_noTokensSaved_navigatedToAuth() = runTest {
        var didNavigateToAuth = false
        notesBaseRule.apply {
            setupNotesClient(
                tokensManager = mockTokensManager(tokenData = TokenData())
            )
            setupNotesRepository()
            setupViewModel()
            composeRule.setCustomContent(uiContent = {
                NotesScreen(viewModel = viewModel,
                    onNavigateToAuth = { didNavigateToAuth = true })
            }) {
                advanceUntilIdle()
                onNodeWithText(getString(R.string.could_not_fetch_notes)).assertDoesNotExist()
                assertTrue(didNavigateToAuth)
            }
        }
    }

    @Test
    fun fetchNotes_fetchFailed_didNotNavigateToAuth() = runTest {
        var didNavigateToAuth = false
        notesBaseRule.apply {
            setupNotesClient(
                notesEngine = mockKtorEngine(
                    content = NotesClientConstants.defaultNotes,
                    status = HttpStatusCode.Unauthorized
                )
            )
            setupNotesRepository()
            setupViewModel()
            composeRule.setCustomContent(uiContent = {
                NotesScreen(viewModel = viewModel,
                    onNavigateToAuth = { didNavigateToAuth = true })
            }) {
                advance()
                onNodeWithText(getString(R.string.could_not_fetch_notes)).assertIsDisplayed()
                assertFalse(didNavigateToAuth)
            }
        }
    }
}