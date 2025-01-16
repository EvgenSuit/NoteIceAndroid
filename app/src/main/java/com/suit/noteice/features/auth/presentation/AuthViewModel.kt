package com.suit.noteice.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suit.noteice.R
import com.suit.noteice.features.auth.domain.AuthRepository
import com.suit.noteice.features.auth.domain.SignInException
import com.suit.noteice.features.auth.domain.UserAlreadyExistsException
import com.suit.noteice.utils.CustomResult
import com.suit.noteice.utils.ktor.InputFieldError
import com.suit.noteice.utils.ktor.InputFieldException
import com.suit.noteice.utils.ui.AuthUIEvent
import com.suit.noteice.utils.ui.UIText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AuthViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(AuthUIState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<AuthUIEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.ChangeAuthType -> _uiState.update { it.copy(
                authType = if (it.authType is AuthType.SignIn) AuthType.SignUp else AuthType.SignIn,
                inputError = null
            ) }
            is AuthIntent.Authenticate -> when(_uiState.value.authType) {
                is AuthType.SignUp -> signUp(intent.email, intent.password)
                is AuthType.SignIn -> signIn(intent.email, intent.password)
            }
        }
    }

    private fun signUp(email: String, password: String) {
        viewModelScope.launch {
            updateAuthResult(CustomResult.InProgress)
            _uiState.update { it.copy(inputError = null) }
            try {
                authRepository.signUp(email, password)
                handleIntent(AuthIntent.ChangeAuthType)
                _uiEvents.emit(AuthUIEvent.ShowSnackbar(UIText.StringResource(R.string.check_your_inbox)))
                updateAuthResult(CustomResult.Success)
            } catch (e: Exception) {
                println(e)
                when (e) {
                    is InputFieldException ->
                        _uiState.update { it.copy(inputError = Json{ignoreUnknownKeys = true}.decodeFromString(e.message!!)) }
                    is UserAlreadyExistsException -> _uiEvents.emit(AuthUIEvent.ShowSnackbar(UIText.StringResource(R.string.user_already_exists)))
                    else -> _uiEvents.emit(AuthUIEvent.ShowSnackbar(UIText.StringResource(R.string.could_not_sign_up)))
                }
                updateAuthResult(CustomResult.Error)
            }
        }
    }

    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            updateAuthResult(CustomResult.InProgress)
            _uiState.update { it.copy(inputError = null) }
            try {
                authRepository.signIn(email, password)
                updateAuthResult(CustomResult.Success)
                _uiEvents.emit(AuthUIEvent.NavigateToNotesScreen)
            } catch (e: Exception) {
                println(e)
                when (e) {
                    is InputFieldException ->
                        _uiState.update { it.copy(inputError = Json{ignoreUnknownKeys = true}.decodeFromString(e.message!!)) }
                    is SignInException -> _uiEvents.emit(AuthUIEvent.ShowSnackbar(UIText.StringResource(R.string.could_not_sign_in_check_credentials)))
                    else -> _uiEvents.emit(AuthUIEvent.ShowSnackbar(UIText.StringResource(R.string.could_not_sign_in)))
                }
                updateAuthResult(CustomResult.Error)
            }
        }
    }

    private fun updateAuthResult(result: CustomResult) =
        _uiState.update { it.copy(authResult = result) }
}

data class AuthUIState(
    val authType: AuthType = AuthType.SignIn,
    val inputError: InputFieldError? = null,
    val authResult: CustomResult = CustomResult.None
)