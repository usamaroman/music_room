package by.eapp.musicroom.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.eapp.musicroom.data.login.repo.AuthAuthenticator
import by.eapp.musicroom.domain.model.LoginData
import by.eapp.musicroom.domain.model.RegistrationData
import by.eapp.musicroom.domain.model.SubmitData
import by.eapp.musicroom.domain.repo.login.AuthorizationService
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    private val _userId = MutableStateFlow<Int>(0)

    private val registerCeh = CoroutineExceptionHandler { _, exc ->
        _stateUi.value = LoginScreenState.Error(exc)
    }

    private fun registerUser(registrationData: RegistrationData) {
        Log.d(TAG, "registerUser called with data: $registrationData")
        viewModelScope.launch(registerCeh + Dispatchers.IO) {
            delay(DELAY_TIME)
            val userId = authService.registerUser(registrationData)
            _userId.value = userId
            delay(DELAY_TIME)
            Log.d(TAG, "registerUser called submit start")
            _stateUi.value = LoginScreenState.SubmitStart
            authService.sendCode(userId)
        }
    }

    fun submitCode(code: String) {
        Log.d(TAG, "submitCode called with code: $code")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = authService.submitCode(SubmitData(_userId.value, code))
                _stateUi.value = LoginScreenState.SubmitComplete
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred while submitting code", e)
            }
        }
    }


    private fun loginUser(loginData: LoginData) {
        _stateUi.value = LoginScreenState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tokens = authService.loginUser(loginData)
                delay(DELAY_TIME)
                _stateUi.value = LoginScreenState.Success
            } catch (e: Exception) {
                _stateUi.value = LoginScreenState.Error(e)
            }
        }
    }

    fun dispatch(action: LoginScreenAction) {
        when (action) {
            is LoginScreenAction.LoginUser -> loginUser(action.loginData)
            is LoginScreenAction.RegisterUser -> registerUser(action.registrationData)
            is LoginScreenAction.SubmitCode -> submitCode(action.code)
        }
    }

    companion object {
        const val DELAY_TIME = 500L
        const val TAG = "AuthorizationViewModel"
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
    data object Success : LoginScreenState
    data object SubmitStart : LoginScreenState
    data object SubmitComplete : LoginScreenState
    data object StartRegistration: LoginScreenState
}
