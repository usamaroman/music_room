package by.eapp.musicroom.domain.repo.auth.login

import by.eapp.musicroom.domain.model.auth.LoginData
import by.eapp.musicroom.domain.model.auth.RegistrationData
import by.eapp.musicroom.domain.model.auth.SubmitData
import by.eapp.musicroom.domain.model.auth.Tokens

interface AuthorizationService {
    suspend fun registerUser(registrationData: RegistrationData): Int
    suspend fun loginUser(loginData: LoginData): Tokens
    suspend fun sendCode(userId: Int?): Unit
    suspend fun submitCode(submitData: SubmitData): String
}