package by.eapp.musicroom.network

import by.eapp.musicroom.network.model.LoginDataDto
import by.eapp.musicroom.network.model.RefreshTokenDto
import by.eapp.musicroom.network.model.RegistrationDataDto
import by.eapp.musicroom.network.model.SubmitDataDto
import by.eapp.musicroom.network.model.TokensDto
import by.eapp.musicroom.network.model.UserIdDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/register")
    suspend fun registerUser(@Body registrationData: RegistrationDataDto): UserIdDto

    @POST("auth/login")
    suspend fun loginUser(@Body loginData: LoginDataDto): TokensDto

    @POST("auth/code/{userId}")
    suspend fun sendCode(@Path("userId") userId: String)

    @POST("auth/submit")
    suspend fun submitCode(@Body userId: SubmitDataDto): Response<String>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: RefreshTokenDto): Response<TokensDto>

}