package by.eapp.musicroom.domain.repo.login

import by.eapp.musicroom.domain.model.LoginData
import by.eapp.musicroom.domain.model.RegistrationData
import by.eapp.musicroom.domain.model.SubmitData
import by.eapp.musicroom.domain.model.Tokens

interface AuthorizationService {
    suspend fun registerUser(registrationData: RegistrationData): Int
    suspend fun loginUser(loginData: LoginData): Tokens
    suspend fun sendCode(userId: Int)
    suspend fun submitCode(submitData: SubmitData): String
    suspend fun refreshToken(refreshToken: String): Tokens
}