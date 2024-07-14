package by.eapp.musicroom.screens.login

interface LoginScreenAction {
    data object StartAuth : LoginScreenAction
//    data class ResponseSsoSettings(val startSSOSettingsData: StartSSOSettingsData) :
//        LoginScreenAction

    //data class CompleteAuth(val authResult: AuthResult) : LoginScreenAction
    data object Success : LoginScreenAction
    data class Error(val error: Throwable?) : LoginScreenAction
}