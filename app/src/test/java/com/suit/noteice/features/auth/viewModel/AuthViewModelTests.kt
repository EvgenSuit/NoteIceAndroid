package com.suit.noteice.features.auth.viewModel

import app.cash.turbine.test
import com.suit.noteice.R
import com.suit.noteice.features.auth.AuthBaseRule
import com.suit.noteice.features.auth.data.AuthRequest
import com.suit.noteice.features.auth.presentation.AuthIntent
import com.suit.noteice.features.auth.presentation.AuthType
import com.suit.noteice.setup.DispatcherRule
import com.suit.noteice.utils.CustomResult
import com.suit.noteice.utils.ktor.InputFieldError
import com.suit.noteice.utils.ui.AuthUIEvent
import com.suit.noteice.utils.ui.UIText
import io.ktor.http.HttpStatusCode
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class AuthViewModelTests {
    @get: Rule(order = 1)
    val authBaseRule = AuthBaseRule()
    @get: Rule(order = 0)
    val dispatcherRule = DispatcherRule(authBaseRule.dispatcher)
    private val inputFieldError = InputFieldError(login = "invalid format", password = "password too short")

    @Test
    fun changeAuthTypeIntent_authTypeChanged() = runTest {
        authBaseRule.apply {
            assertEquals(AuthType.SignIn, viewModel.uiState.value.authType)
            viewModel.handleIntent(AuthIntent.ChangeAuthType)
            assertEquals(AuthType.SignUp, viewModel.uiState.value.authType)
        }
    }

    @Test
    fun signUp_invalidInput_inputException_changeAuthType_inputErrorNull() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = inputFieldError,
                statusCode = HttpStatusCode.BadRequest
            )
            setupViewModel()

            viewModel.handleIntent(AuthIntent.ChangeAuthType)
            viewModel.handleIntent(AuthIntent.Authenticate("", ""))
            viewModel.uiState.test {
                skipItems(1)

                assertEquals(CustomResult.InProgress, awaitItem().authResult)
                assertEquals(inputFieldError, awaitItem().inputError)
                assertEquals(CustomResult.Error, awaitItem().authResult)
            }
            coVerify(exactly = 1) { authClient.signUp(AuthRequest("", "")) }

            viewModel.handleIntent(AuthIntent.ChangeAuthType)
            assertEquals(null, viewModel.uiState.value.inputError)
        }
    }
    @Test
    fun signUp_userAlreadyExists_exception() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = Unit,
                statusCode = HttpStatusCode.Conflict
            )
            setupViewModel()

            viewModel.handleIntent(AuthIntent.ChangeAuthType)
            viewModel.handleIntent(AuthIntent.Authenticate("", ""))
            viewModel.uiState.test {
                skipItems(1)
                startEventsCollection()

                assertEquals(CustomResult.InProgress, awaitItem().authResult)
                assertEquals(CustomResult.Error, awaitItem().authResult)
                stopEventsCollection()
            }
            coVerify(exactly = 1) { authClient.signUp(AuthRequest("", "")) }
            assertEquals(listOf(AuthUIEvent.ShowSnackbar(UIText.StringResource(R.string.user_already_exists))),
                uiEvents.toList())
        }
    }
    @Test
    fun signUp_unknownError_exception() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = Unit,
                statusCode = HttpStatusCode.Forbidden
            )
            setupViewModel()

            viewModel.handleIntent(AuthIntent.ChangeAuthType)
            viewModel.handleIntent(AuthIntent.Authenticate("", ""))
            viewModel.uiState.test {
                skipItems(1)
                startEventsCollection()

                assertEquals(CustomResult.InProgress, awaitItem().authResult)
                assertEquals(CustomResult.Error, awaitItem().authResult)
                stopEventsCollection()
            }
            coVerify(exactly = 1) { authClient.signUp(AuthRequest("", "")) }
            assertEquals(listOf(AuthUIEvent.ShowSnackbar(UIText.StringResource(R.string.could_not_sign_up))),
                uiEvents.toList())
        }
    }
    @Test
    fun signUp_success_confirmationSnackbarShown() = runTest {
        authBaseRule.apply {
            viewModel.handleIntent(AuthIntent.ChangeAuthType)
            viewModel.handleIntent(AuthIntent.Authenticate("", ""))
            viewModel.uiState.test {
                skipItems(1)
                startEventsCollection()

                assertEquals(CustomResult.InProgress, awaitItem().authResult)
                assertEquals(AuthType.SignIn, awaitItem().authType)
                assertEquals(CustomResult.Success, awaitItem().authResult)
                stopEventsCollection()
            }
            coVerify(exactly = 1) { authClient.signUp(AuthRequest("", "")) }
            assertEquals(listOf(AuthUIEvent.ShowSnackbar(UIText.StringResource(R.string.email_confirmation))),
                uiEvents.toList())
        }
    }

    @Test
    fun signIn_invalidInput_inputException_changeAuthType_inputErrorNull() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = inputFieldError,
                statusCode = HttpStatusCode.BadRequest
            )
            setupViewModel()

            viewModel.handleIntent(AuthIntent.Authenticate("", ""))
            viewModel.uiState.test {
                skipItems(1)

                assertEquals(CustomResult.InProgress, awaitItem().authResult)
                assertEquals(inputFieldError, awaitItem().inputError)
                assertEquals(CustomResult.Error, awaitItem().authResult)
            }
            coVerify(exactly = 1) { authClient.signIn(AuthRequest("", "")) }

            viewModel.handleIntent(AuthIntent.ChangeAuthType)
            assertEquals(null, viewModel.uiState.value.inputError)
        }
    }
    @Test
    fun signIn_signInException_exception() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = Unit,
                statusCode = HttpStatusCode.Unauthorized
            )
            setupViewModel()

            viewModel.handleIntent(AuthIntent.Authenticate("", ""))
            viewModel.uiState.test {
                skipItems(1)
                startEventsCollection()

                assertEquals(CustomResult.InProgress, awaitItem().authResult)
                assertEquals(CustomResult.Error, awaitItem().authResult)
                stopEventsCollection()
            }
            coVerify(exactly = 1) { authClient.signIn(AuthRequest("", "")) }
            assertEquals(listOf(AuthUIEvent.ShowSnackbar(UIText.StringResource(R.string.could_not_sign_in_check_credentials))),
                uiEvents.toList())
        }
    }
    @Test
    fun signIn_unknownError_exception() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = Unit,
                statusCode = HttpStatusCode.Forbidden
            )
            setupViewModel()

            viewModel.handleIntent(AuthIntent.Authenticate("", ""))
            viewModel.uiState.test {
                skipItems(1)
                startEventsCollection()

                assertEquals(CustomResult.InProgress, awaitItem().authResult)
                assertEquals(CustomResult.Error, awaitItem().authResult)
                stopEventsCollection()
            }
            coVerify(exactly = 1) { authClient.signIn(AuthRequest("", "")) }
            assertEquals(listOf(AuthUIEvent.ShowSnackbar(UIText.StringResource(R.string.could_not_sign_in))),
                uiEvents.toList())
        }
    }
    @Test
    fun signIn_success_navigateToNotesScreenEventEmitted() = runTest {
        authBaseRule.apply {
            startEventsCollection()
            viewModel.handleIntent(AuthIntent.Authenticate("", ""))
            viewModel.uiState.test {
                skipItems(1)

                assertEquals(CustomResult.InProgress, awaitItem().authResult)
                assertEquals(CustomResult.Success, awaitItem().authResult)
                stopEventsCollection()
            }
            coVerify(exactly = 1) { authClient.signIn(AuthRequest("", "")) }
            assertEquals(listOf(AuthUIEvent.NavigateToNotesScreen), uiEvents.toList())
        }
    }
}