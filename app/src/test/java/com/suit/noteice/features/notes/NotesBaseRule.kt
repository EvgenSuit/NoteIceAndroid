package com.suit.noteice.features.notes

import com.suit.noteice.features.notes.domain.NotesRepository
import com.suit.noteice.features.notes.presentation.NotesViewModel
import com.suit.noteice.setup.notes.mockNotesClient
import com.suit.noteice.utils.notes.NotesClient
import com.suit.noteice.utils.notes.TokensManager
import com.suit.noteice.utils.ui.NotesUIEvent
import io.ktor.client.engine.mock.MockEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class NotesBaseRule: TestWatcher() {
    val testDispatcher = StandardTestDispatcher()
    lateinit var viewModel: NotesViewModel
    lateinit var notesRepository: NotesRepository
    lateinit var notesClient: NotesClient

    private var uiEventCollectJob: Job? = null
    val collectedUIEvents = mutableListOf<NotesUIEvent>()

    fun setupNotesClient(
        refreshTokenEngine: MockEngine? = null,
        notesEngine: MockEngine? = null,
        tokensManager: TokensManager? = null
    ) {
        notesClient = mockNotesClient(
            inputRefreshTokenEngine = refreshTokenEngine,
            inputNotesEngine = notesEngine,
            inputTokensManager = tokensManager,
            dispatcher = testDispatcher
        )
    }

    fun setupNotesRepository() {
        notesRepository = NotesRepository(
            notesClient = notesClient
        )
    }

    fun setupViewModel() {
        viewModel = NotesViewModel(
            notesRepository = notesRepository
        )
    }

    override fun starting(description: Description?) {
        setupNotesClient()
        setupNotesRepository()
        setupViewModel()
    }

    fun TestScope.advance() {
        repeat(9999999) {
            advanceUntilIdle()
        }
    }

    suspend fun TestScope.startUIEventCollection() {
        uiEventCollectJob = launch {
            viewModel.uiEvent.collectLatest {
                collectedUIEvents.add(it)
            }
        }
    }
    fun stopUIEventCollection() = uiEventCollectJob!!.cancel()
}