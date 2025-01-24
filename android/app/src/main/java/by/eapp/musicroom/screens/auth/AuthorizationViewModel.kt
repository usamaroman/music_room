package by.eapp.musicroom.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.eapp.musicroom.data.login.repo.AuthAuthenticator
import by.eapp.musicroom.domain.model.LoginData
import by.eapp.musicroom.domain.model.RegistrationData
import by.eapp.musicroom.domain.model.SubmitData
import by.eapp.musicroom.domain.repo.login.AuthorizationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val auth: AuthAuthenticator,
    private val authService: AuthorizationService,
) : ViewModel() {

    private val _stateUi = MutableStateFlow<LoginScreenState>(LoginScreenState.StartRegistration)
    val stateUi = _stateUi.asStateFlow()

    private val _userId = MutableStateFlow(0)

    private val exceptionHandler = CoroutineExceptionHandler { _, exc ->
        updateState(LoginScreenState.Error(exc))
    }

    fun dispatch(action: LoginScreenAction) {
        when (action) {
            is LoginScreenAction.LoginUser -> handleAction { loginUser(action.loginData) }
            is LoginScreenAction.RegisterUser -> handleAction { registerUser(action.registrationData) }
            is LoginScreenAction.SubmitCode -> handleAction { submitCode(action.code) }
        }
    }

    private fun handleAction(action: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            updateState(LoginScreenState.Loading)
            action()
        }
    }

    private suspend fun registerUser(registrationData: RegistrationData) {
        logDebug("registerUser called with data: $registrationData")
        val userId = authService.registerUser(registrationData)
        logDebug("User registered successfully with ID: $userId")

        _userId.value = userId
        authService.sendCode(userId)
        updateState(LoginScreenState.SubmitStart)
    }

    private suspend fun submitCode(code: String) {
        logDebug("submitCode called with code: $code")
        authService.submitCode(SubmitData(_userId.value, code))
        updateState(LoginScreenState.SubmitComplete)
    }

    private suspend fun loginUser(loginData: LoginData) {
        logDebug("loginUser called with data: $loginData")
        authService.loginUser(loginData)
        updateState(LoginScreenState.LoginSuccess)
    }

    private fun updateState(state: LoginScreenState) {
        _stateUi.value = state
    }

    private fun logDebug(message: String) {
        Log.d(TAG, message)
    }

    companion object {
        private const val TAG = "AuthorizationViewModel"
    }
}

sealed interface LoginScreenAction {
    data class LoginUser(val loginData: LoginData) : LoginScreenAction
    data class RegisterUser(val registrationData: RegistrationData) : LoginScreenAction
    data class SubmitCode(val code: String) : LoginScreenAction
}

sealed interface LoginScreenState {
    data object Loading : LoginScreenState
    data class Error(val error: Throwable?) : LoginScreenState
    data object StartRegistration : LoginScreenState
    data object LoginSuccess : LoginScreenState
    data object SubmitStart : LoginScreenState
    data object SubmitComplete : LoginScreenState
}

