package by.eapp.musicroom.screens.login

sealed interface LoginScreenState {
    data object Init : LoginScreenState
    data object Loading : LoginScreenState

    // data class AuthWithSdkSso(val startSSOSettingsData: StartSSOSettingsData) : LoginScreenState
    data class Error(val error: Throwable?) : LoginScreenState
    data object Success : LoginScreenState
}