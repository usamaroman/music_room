package by.eapp.musicroom.data.login.repo

import android.util.Log
import by.eapp.musicroom.domain.model.LoginData
import by.eapp.musicroom.domain.model.RegistrationData
import by.eapp.musicroom.domain.model.SubmitData
import by.eapp.musicroom.domain.model.Tokens
import by.eapp.musicroom.domain.repo.login.AuthorizationService
import by.eapp.musicroom.domain.repo.login.JwtTokenManager
import by.eapp.musicroom.network.model.RefreshTokenDto
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenManager: JwtTokenManager,
    private val auth: AuthorizationService,
) : Authenticator {

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val TOKEN_TYPE = "Bearer"
    }


    override fun authenticate(route: Route?, response: Response): Request? {
        val currentToken = runBlocking {
            tokenManager.getAccessJwt()
        }
        synchronized(this) {
            val updatedToken = runBlocking {
                tokenManager.getAccessJwt()
            }
            val token = if (currentToken != updatedToken) updatedToken else {
                val newSessionResponse = runBlocking {
                    updatedToken?.let {
                        RefreshTokenDto(
                            it
                        )
                    }?.let { auth.refreshToken(updatedToken) }
                }
                newSessionResponse?.let { body ->
                    runBlocking {
                        tokenManager.saveAccessJwt(body.accessToken)
                        tokenManager.saveRefreshJwt(body.refreshToken)
                    }
                    body.accessToken
                }
            }
            return if (token != null) response.request.newBuilder()
                .header(HEADER_AUTHORIZATION, "$token")
                .build() else null
        }
    }

    suspend fun registerUser(registrationData: RegistrationData): Int {
        val result = auth.registerUser(registrationData)
        Log.d("AuthAuthenticator", "registerUser: $result")
        return result
    }

    suspend fun loginUser(loginData: LoginData): Tokens {
        val result = auth.loginUser(loginData)
        Log.d("AuthAuthenticator", "access token: ${result.accessToken}")
        Log.d("AuthAuthenticator", "refresh token: ${result.refreshToken}")
        return result
    }

    suspend fun sendCode(userId: Int) {
        auth.sendCode(userId)
    }

    suspend fun submitCode(submitData: SubmitData): String {
        val result = auth.submitCode(submitData)
        Log.d("AuthAuthenticator", "submitCode: $result")
        return result
    }

    suspend fun refreshToken(refreshToken: String): Tokens {
        val result = auth.refreshToken(refreshToken)
        Log.d("AuthAuthenticator", "access token: ${result.accessToken}")
        Log.d("AuthAuthenticator", "refresh token: ${result.refreshToken}")
        return result
    }

}