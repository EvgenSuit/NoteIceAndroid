package com.suit.noteice.features.auth.presentation.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.suit.noteice.features.auth.presentation.AuthIntent
import com.suit.noteice.features.auth.presentation.AuthType
import com.suit.noteice.features.auth.presentation.AuthUIState
import com.suit.noteice.features.auth.presentation.AuthViewModel
import com.suit.noteice.features.auth.presentation.ui.components.AuthColumn
import com.suit.noteice.ui.theme.NoteIceTheme
import com.suit.noteice.utils.CustomResult
import com.suit.noteice.utils.ktor.InputFieldError
import com.suit.noteice.utils.ui.AuthUIEvent
import com.suit.noteice.utils.ui.LocalSnackbarProvider
import com.suit.noteice.utils.ui.commonComponents.ConstrainedColumn
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onNavigateToNotesScreen: () -> Unit
) {
    val snackbarController = LocalSnackbarProvider.current
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is AuthUIEvent.ShowSnackbar -> snackbarController.showSnackbar(event.text)
                is AuthUIEvent.NavigateToNotesScreen -> onNavigateToNotesScreen()
            }
        }
    }
    AuthScreenContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent
    )
}

@Composable
fun AuthScreenContent(
    uiState: AuthUIState,
    onIntent: (AuthIntent) -> Unit
) {
    val authEnabled = uiState.authResult !is CustomResult.InProgress
    ConstrainedColumn(
        modifier = Modifier.padding(10.dp)
    ) {
        Crossfade(targetState = uiState.authType, label = "AuthContentCrossfade") { authType ->
            when (authType) {
                is AuthType.SignIn -> {
                    AuthColumn(
                        authType = authType,
                        enabled = authEnabled,
                        inputError = uiState.inputError,
                        onAuth = { email, password -> onIntent(AuthIntent.Authenticate(email, password)) },
                        onAuthTypeChange = { onIntent(AuthIntent.ChangeAuthType) }
                    )
                }
                is AuthType.SignUp -> {
                    AuthColumn(
                        authType = authType,
                        enabled = authEnabled,
                        inputError = uiState.inputError,
                        onAuth = { email, password -> onIntent(AuthIntent.Authenticate(email, password)) },
                        onAuthTypeChange = { onIntent(AuthIntent.ChangeAuthType) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    NoteIceTheme {
        Surface {
            AuthScreenContent(
                uiState = AuthUIState(
                    inputError = InputFieldError(login = "Invalid login format", password = "Password is too short"),
                    authType = AuthType.SignIn
                ),
                onIntent = {})
        }
    }
}