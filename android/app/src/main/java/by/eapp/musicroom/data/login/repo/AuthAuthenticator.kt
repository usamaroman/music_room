package by.eapp.musicroom.data.login.repo

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
    private val apiService: RefreshTokenServiceImpl,
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
                    }?.let { apiService.refreshToken(updatedToken) }
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
                .header(HEADER_AUTHORIZATION, "$TOKEN_TYPE token")
                .build() else null
        }
    }
}