package by.eapp.musicroom.domain.repo.login

import by.eapp.musicroom.domain.model.RegistrationData
import by.eapp.musicroom.domain.model.SubmitData

interface AuthorizationService {
    suspend fun registerUser(registrationData: RegistrationData)
    suspend fun loginUser(login: String, password: String)
    suspend fun sendCode(userId: Int)
    suspend fun submitCode(submitData: SubmitData)
    suspend fun refreshToken(refreshToken: String)
}