package by.eapp.musicroom.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.eapp.musicroom.domain.model.LoginData
import by.eapp.musicroom.domain.model.RegistrationData
import by.eapp.musicroom.domain.model.SubmitData
import by.eapp.musicroom.domain.repo.login.AuthorizationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val auth: AuthorizationService,
) : ViewModel() {
    private val _stateUi = MutableStateFlow<LoginScreenState>(LoginScreenState.Init)
    val stateUi = _stateUi.asStateFlow()

    fun registerUser(registrationData: RegistrationData) {
        _stateUi.value = LoginScreenState.Loading
        viewModelScope.launch {
            val userId = auth.registerUser(registrationData)
            delay(DELAY_TIME)
            auth.sendCode(userId)
        }
    }


    fun submitCode(submitData: SubmitData) {
        _stateUi.value = LoginScreenState.Loading
        viewModelScope.launch {
            val token = auth.submitCode(submitData)
            delay(DELAY_TIME)
            _stateUi.value = LoginScreenState.Success
        }
    }

    fun loginUser(loginData: LoginData) {
        _stateUi.value = LoginScreenState.Loading
        viewModelScope.launch {
            val tokens = auth.loginUser(loginData)
            delay(DELAY_TIME)
            auth.refreshToken(tokens.refreshToken)
            _stateUi.value = LoginScreenState.Success
        }
    }

    fun dispatch(action: LoginScreenAction) {
        _stateUi.value = when (action) {
            is LoginScreenAction.StartAuth -> LoginScreenState.Loading
            is LoginScreenAction.Success -> LoginScreenState.Success
            is LoginScreenAction.Error -> LoginScreenState.Error(action.error)
        }
    }

    companion object {
        const val DELAY_TIME = 500L
    }

}

sealed interface RegistrationScreenAction {
    data object Loading : RegistrationScreenAction
    data object Success : RegistrationScreenAction
    data class Error(val error: Throwable?) : RegistrationScreenAction
}

sealed interface LoginScreenAction {
    data object StartAuth : LoginScreenAction
    data object Success : LoginScreenAction
    data class Error(val error: Throwable?) : LoginScreenAction
}

sealed interface LoginScreenState {
    data object Init : LoginScreenState
    data object Loading : LoginScreenState
    data class Error(val error: Throwable?) : LoginScreenState
    data object Success : LoginScreenState
}
