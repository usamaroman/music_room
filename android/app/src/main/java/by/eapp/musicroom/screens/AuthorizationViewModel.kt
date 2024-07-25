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
    private val _stateUi = MutableStateFlow<LoginScreenState>(LoginScreenState.Init)
    val stateUi = _stateUi.asStateFlow()

    private fun registerUser(registrationData: RegistrationData) {
        _stateUi.value = LoginScreenState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = authService.registerUser(registrationData)
                Log.d(
                    TAG,
                    "------------email: ${registrationData.email}  ${registrationData.nickname}"
                )
                delay(DELAY_TIME)
                authService.sendCode(userId)
                Log.d(TAG, "------------userId: $userId ")
                _stateUi.value = LoginScreenState.Success
            } catch (e: Exception) {
                _stateUi.value = LoginScreenState.Error(e)
            }
        }
    }


    private fun submitCode(submitData: SubmitData) {
        _stateUi.value = LoginScreenState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val token = authService.submitCode(submitData)
            Log.d(TAG, "------------token: $token ")
            delay(DELAY_TIME)
            _stateUi.value = LoginScreenState.Success
        }
    }

    private fun loginUser(loginData: LoginData) {
        _stateUi.value = LoginScreenState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tokens = authService.loginUser(loginData)
                Log.d(TAG, "------------tokens: $tokens ")
                delay(DELAY_TIME)
                //refresh
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
            is LoginScreenAction.SubmitCode -> submitCode(action.submitData)
            is LoginScreenAction.ShowError -> showToast(action.text, context = action.context)
        }
    }

    companion object {
        const val DELAY_TIME = 500L
        const val TAG = "AuthorizationViewModel"
    }

    sealed class Effect(private var isHandled: Boolean = false) {
        fun runIfNotHandled(action: (Effect) -> Unit) {
            if (!isHandled) {
                action(this)
                isHandled = true
            }
        }

        class Exit : Effect(false)
        class None : Effect(true)
    }

    fun showToast(text: String, context: Context) {
        Toast.makeText(getApplication(context), text, Toast.LENGTH_SHORT).show()
    }

    fun enableButton(
        email: String,
        password: String,
        repeatPassword: String,
        nickname: String,
    ): Boolean {
        return (email.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty() && nickname.isNotEmpty())
    }

}

sealed interface LoginScreenAction {
    data class LoginUser(val loginData: LoginData) : LoginScreenAction
    data class RegisterUser(val registrationData: RegistrationData) : LoginScreenAction
    data class SubmitCode(val submitData: SubmitData) : LoginScreenAction
    data class ShowError(val text: String, val context: Context) : LoginScreenAction
}

sealed interface LoginScreenState {
    data object Init : LoginScreenState
    data object Loading : LoginScreenState
    data class Error(val error: Throwable?) : LoginScreenState
    data object Success : LoginScreenState
}
