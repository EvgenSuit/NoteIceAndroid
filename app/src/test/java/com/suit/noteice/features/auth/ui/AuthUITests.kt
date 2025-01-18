package com.suit.noteice.features.auth.ui

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.suit.noteice.R
import com.suit.noteice.features.auth.AuthBaseRule
import com.suit.noteice.features.auth.presentation.ui.AuthScreen
import com.suit.noteice.features.auth.presentation.ui.components.AuthFieldType
import com.suit.noteice.setup.DispatcherRule
import com.suit.noteice.utils.ktor.InputFieldError
import com.suit.noteice.utils.ui.getString
import com.suit.noteice.utils.ui.setCustomContent
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AuthUITests {
    @get: Rule(order = 1)
    val authBaseRule = AuthBaseRule()
    @get: Rule(order = 0)
    val dispatcherRule = DispatcherRule(authBaseRule.dispatcher)
    @get: Rule
    val composeRule = createComposeRule()
    private val inputFieldError = InputFieldError(login = "invalid format", password = "password too short")
    private val email = "email@gmail.com"
    private val password = "Password123$"

    @Test
    fun changeAuthTypeIntent_authTypeChanged() = runTest {
        authBaseRule.apply {
            composeRule.setCustomContent(uiContent = {
                AuthScreen(
                    viewModel = viewModel,
                    onNavigateToNotesScreen = {}
                )
            }) {
                onNodeWithText(getString(R.string.email)).assertExists()
                onNodeWithText(getString(R.string.password)).assertExists()
                onNodeWithText(getString(R.string.sign_in)).assertExists().assertIsNotEnabled()
                onNodeWithText(getString(R.string.do_not_have_account)).assertExists()
                goToSignUp()


                onNodeWithText(getString(R.string.email)).assertExists()
                onNodeWithText(getString(R.string.password)).assertExists()
                onNodeWithText(getString(R.string.sign_up)).assertExists().assertIsNotEnabled()
                goToSignIn()

                onNodeWithText(getString(R.string.sign_in)).assertExists()
            }
        }
    }
    @Test
    fun performFieldInput_exceedsMaxLength() = runTest {
        authBaseRule.apply {
            composeRule.setCustomContent(uiContent = {
                AuthScreen(
                    viewModel = viewModel,
                    onNavigateToNotesScreen = {}
                )
            }) {
                onNodeWithContentDescription(Icons.Filled.VisibilityOff.name).performClick()
                val maxAuthFieldLength = ApplicationProvider.getApplicationContext<Context>()
                    .resources.getInteger(R.integer.max_auth_field_length)
                val longText = "t".repeat(maxAuthFieldLength+1)
                for (c in longText) {
                    onNodeWithTag(AuthFieldType.Email.toString()).performTextInput(c.toString())
                    onNodeWithTag(AuthFieldType.Password.toString()).performTextInput(c.toString())
                }
                onNodeWithTag(AuthFieldType.Email.toString()).assertTextEquals(longText.take(maxAuthFieldLength))
                onNodeWithTag(AuthFieldType.Password.toString()).assertTextEquals(longText.take(maxAuthFieldLength))
            }
        }
    }
    @Test
    fun hidePassword_passwordHidden() = runTest {
        authBaseRule.apply {
            composeRule.setCustomContent(uiContent = {
                AuthScreen(
                    viewModel = viewModel,
                    onNavigateToNotesScreen = {}
                )
            }) {
                val email = "email"
                val password = "password"
                onNodeWithTag(AuthFieldType.Email.toString()).performTextInput(email)
                onNodeWithTag(AuthFieldType.Password.toString()).performTextInput(password)
                onNodeWithText(email).assertExists()
                onNodeWithText(password).assertDoesNotExist()

                // this icon is shown when password is visible
                val hidePasswordIcon = onNodeWithContentDescription(Icons.Filled.Visibility.name)
                // this icon is shown when password is NOT visible
                val showPasswordIcon = onNodeWithContentDescription(Icons.Filled.VisibilityOff.name)
                hidePasswordIcon.assertDoesNotExist()
                showPasswordIcon.assertExists()

                showPasswordIcon.performScrollTo().performClick()

                showPasswordIcon.assertDoesNotExist()
                hidePasswordIcon.assertIsDisplayed()
                onNodeWithText(email).assertExists()
                onNodeWithText(password).assertExists()
            }
        }
    }

    @Test
    fun signUp_invalidInput_inputErrorShown() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = inputFieldError,
                statusCode = HttpStatusCode.BadRequest
            )
            setupViewModel()

            composeRule.setCustomContent(uiContent = {
                AuthScreen(
                    viewModel = viewModel,
                    onNavigateToNotesScreen = {}
                )
            }) {
                goToSignUp()
                performSignUp()
                advance()

                onNodeWithText(inputFieldError.login!!).assertExists()
                onNodeWithText(inputFieldError.password!!).assertExists()
            }
        }
    }

    @Test
    fun signUp_userAlreadyExists_snackbarShown() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = Unit,
                statusCode = HttpStatusCode.Conflict
            )
            setupViewModel()

            composeRule.setCustomContent(uiContent = {
                AuthScreen(
                    viewModel = viewModel,
                    onNavigateToNotesScreen = {}
                )
            }) {
                goToSignUp()
                performSignUp()
                advance()
                waitForIdle()
                onNodeWithText(getString(R.string.user_already_exists)).assertIsDisplayed()
            }
        }
    }
    @Test
    fun signUp_unknownError_snackbarShown() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = Unit,
                statusCode = HttpStatusCode.Forbidden
            )
            setupViewModel()

            composeRule.setCustomContent(uiContent = {
                AuthScreen(
                    viewModel = viewModel,
                    onNavigateToNotesScreen = {}
                )
            }) {
                goToSignUp()
                performSignUp()
                advance()
                waitForIdle()
                onNodeWithText(getString(R.string.could_not_sign_up)).assertIsDisplayed()
            }
        }
    }
    @Test
    fun signUp_success_confirmationSnackbarShown() = runTest {
        authBaseRule.apply {
            composeRule.setCustomContent(uiContent = {
                AuthScreen(
                    viewModel = viewModel,
                    onNavigateToNotesScreen = {}
                )
            }) {
                goToSignUp()
                performSignUp()
                advance()
                waitForIdle()
                onNodeWithText(getString(R.string.email_confirmation)).assertIsDisplayed()
            }
        }
    }

    @Test
    fun signIn_invalidInput_inputError() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = inputFieldError,
                statusCode = HttpStatusCode.BadRequest
            )
            setupViewModel()

            composeRule.setCustomContent(uiContent = {
                AuthScreen(
                    viewModel = viewModel,
                    onNavigateToNotesScreen = {}
                )
            }) {
                performSignIn()
                advance()
                onNodeWithText(inputFieldError.login!!).assertExists()
                onNodeWithText(inputFieldError.password!!).assertExists()
            }
        }
    }
    @Test
    fun signIn_signInException_snackbarShown() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = Unit,
                statusCode = HttpStatusCode.Unauthorized
            )
            setupViewModel()

            composeRule.setCustomContent(uiContent = {
                AuthScreen(
                    viewModel = viewModel,
                    onNavigateToNotesScreen = {}
                )
            }) {
                performSignIn()
                advance()
                waitForIdle()
                onNodeWithText(getString(R.string.could_not_sign_in_check_credentials)).assertIsDisplayed()
            }
        }
    }
    @Test
    fun signIn_unknownError_snackbarShown() = runTest {
        authBaseRule.apply {
            setupRepository(
                response = Unit,
                statusCode = HttpStatusCode.Forbidden
            )
            setupViewModel()

            composeRule.setCustomContent(uiContent = {
                AuthScreen(
                    viewModel = viewModel,
                    onNavigateToNotesScreen = {}
                )
            }) {
                performSignIn()
                advance()
                waitForIdle()
                onNodeWithText(getString(R.string.could_not_sign_in)).assertIsDisplayed()
            }
        }
    }
    @Test
    fun signIn_success_navigatedToNotesScreen() = runTest {
        var navigatedToAuth = false
        authBaseRule.apply {
            composeRule.setCustomContent(uiContent = {
                AuthScreen(
                    viewModel = viewModel,
                    onNavigateToNotesScreen = { navigatedToAuth = true }
                )
            }) {
                performSignIn()
                advance()
                waitForIdle()
                assertTrue(navigatedToAuth)
            }
        }
    }

    private fun ComposeContentTestRule.goToSignUp() = onNodeWithText(getString(R.string.go_to_signup)).assertExists().performScrollTo().performClick()
    private fun ComposeContentTestRule.goToSignIn() = onNodeWithContentDescription(Icons.AutoMirrored.Filled.ArrowBack.name).assertIsDisplayed().performClick()
    private fun ComposeContentTestRule.performSignUp() {
        onNodeWithTag(AuthFieldType.Email.toString()).performTextInput(email)
        onNodeWithTag(AuthFieldType.Password.toString()).performTextInput(password)
        onNodeWithText(getString(R.string.sign_up)).performScrollTo().assertIsEnabled().performClick()
    }
    private fun ComposeContentTestRule.performSignIn() {
        onNodeWithTag(AuthFieldType.Email.toString()).performTextInput(email)
        onNodeWithTag(AuthFieldType.Password.toString()).performTextInput(password)
        onNodeWithText(getString(R.string.sign_in)).performScrollTo().assertIsEnabled().performClick()
    }
}