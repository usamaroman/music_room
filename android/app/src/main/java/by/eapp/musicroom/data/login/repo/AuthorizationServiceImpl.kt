package by.eapp.musicroom.data.login.repo

import by.eapp.musicroom.data.RegistrationMappers.toDto
import by.eapp.musicroom.domain.model.auth.LoginData
import by.eapp.musicroom.domain.model.auth.RegistrationData
import by.eapp.musicroom.domain.model.auth.SubmitData
import by.eapp.musicroom.domain.model.auth.Tokens
import by.eapp.musicroom.domain.repo.auth.login.AuthorizationService
import by.eapp.musicroom.domain.repo.auth.login.JwtTokenManager
import by.eapp.musicroom.network.AuthorizationApiService
import javax.inject.Inject

class AuthorizationServiceImpl @Inject constructor(
    private val apiService: AuthorizationApiService,
    private val tokenManager: JwtTokenManager,
) : AuthorizationService {
    override suspend fun registerUser(registrationData: RegistrationData): Int {
        val response = apiService.registerUser(registrationData.toDto())
        if (response.isSuccessful) {
            val result = response.body()
            if (result != null) {
                return result.userId
            } else {
                throw Exception("Response body is null")
            }
        } else {
            throw Exception("Failed to register user: ${response.errorBody()?.string()}")
        }
    }


    override suspend fun loginUser(loginData: LoginData): Tokens {
        val response = apiService.loginUser(loginData.toDto())

        if (response.isSuccessful) {
            val result = response.body()
            if (result != null) {
                tokenManager.saveAccessJwt(result.accessToken)
                tokenManager.saveRefreshJwt(result.refreshToken)
                return Tokens(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken
                )
            } else {
                throw Exception("Response body is null")
            }
        } else {
            throw Exception("Failed to login user: ${response.errorBody()?.string()}")
        }
    }


    override suspend fun sendCode(userId: Int?): Unit {
        if (userId == null) throw Exception("U send null user id")
        else apiService.sendCode(userId = userId)
    }

    override suspend fun submitCode(submitData: SubmitData): String {
        val response = apiService.submitCode(submitData.toDto())
        if (response.isSuccessful) {
            val result = response.body()
            if (result != null) {
                return result
            } else {
                throw Exception("Response body is null")
            }
        } else {
            throw Exception("Failed to submit code: ${response.errorBody()?.string()}")
        }
    }


}