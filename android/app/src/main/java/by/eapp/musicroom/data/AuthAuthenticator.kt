package by.eapp.musicroom.data

import by.eapp.musicroom.domain.model.RegistrationData
import by.eapp.musicroom.domain.model.SubmitData
import by.eapp.musicroom.domain.repo.login.AuthorizationService
import by.eapp.musicroom.domain.repo.login.JwtTokenManager
import by.eapp.musicroom.network.ApiService
import okhttp3.Authenticator
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenManager: JwtTokenManager,
    private val apiService: ApiService,
) : Authenticator, AuthorizationService {

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val TOKEN_TYPE = "Bearer"
    }


//    override fun authenticate(route: Route?, response: Response): Request? {
//        val currentToken = runBlocking {
//            tokenManager.getAccessJwt()
//        }
//        synchronized(this) {
//            val updatedToken = runBlocking {
//                tokenManager.getAccessJwt()
//            }
//            val token = if (currentToken != updatedToken) updatedToken else {
//                val newSessionResponse = runBlocking { apiService.refreshToken("") }
//                if (newSessionResponse.isSuccessful && newSessionResponse.body() != null) {
//                    newSessionResponse.body()?.let { body ->
//                        runBlocking {
//                            tokenManager.saveAccessJwt(body.accessToken)
//                            tokenManager.saveRefreshJwt(body.refreshToken)
//                        }
//                        body.accessToken
//                    }
//                } else null
//            }
//            return if (token != null) response.request.newBuilder()
//                .header(HEADER_AUTHORIZATION, "$token")
//                .build() else null
//        }
//    }

    override suspend fun registerUser(registrationData: RegistrationData) {
        TODO("Not yet implemented")
    }

    override suspend fun loginUser(login: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun sendCode(userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun submitCode(submitData: SubmitData) {
        TODO("Not yet implemented")
    }

    override suspend fun refreshToken(refreshToken: String) {
        TODO("Not yet implemented")
    }
}