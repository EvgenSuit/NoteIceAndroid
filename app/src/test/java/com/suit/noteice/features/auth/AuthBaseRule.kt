package com.suit.noteice.features.auth

import com.suit.noteice.features.auth.domain.AuthClient
import com.suit.noteice.features.auth.domain.AuthRepository
import com.suit.noteice.features.auth.presentation.AuthViewModel
import com.suit.noteice.setup.auth.mockTokensManager
import com.suit.noteice.setup.ktor.mockKtorEngine
import com.suit.noteice.utils.ui.AuthUIEvent
import io.ktor.http.HttpStatusCode
import io.mockk.spyk
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.Locale

class AuthBaseRule: TestWatcher() {
    val dispatcher = StandardTestDispatcher()

    lateinit var uiEvents: MutableList<AuthUIEvent>
    private lateinit var uiEventsCollectionJob: Job

    lateinit var viewModel: AuthViewModel
    lateinit var repository: AuthRepository
    lateinit var authClient: AuthClient

    inline fun <reified T> setupRepository(
        response: T,
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        locale: Locale = Locale.US
    ) {
        authClient = spyk(AuthClient(
            engine = mockKtorEngine(
                content = response,
                status = statusCode
            ),
            locale = locale,
            tokensManager = mockTokensManager()
        ))
        repository = spyk(AuthRepository(
            authClient = authClient
        ))
    }
    fun setupViewModel() {
        viewModel = AuthViewModel(authRepository = repository)
    }

    override fun starting(description: Description?) {
        uiEventsCollectionJob = Job()
        uiEvents = mutableListOf()
        setupRepository(response = Unit)
        setupViewModel()
    }

    fun TestScope.startEventsCollection() {
        uiEventsCollectionJob = launch {
            viewModel.uiEvents.collectLatest {
                println(it)
                uiEvents.add(it) }
        }
    }
    fun TestScope.stopEventsCollection() = uiEventsCollectionJob.cancel()

    fun TestScope.advance() = repeat(9999999) {
        this.advanceUntilIdle()
    }
}