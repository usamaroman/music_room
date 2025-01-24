package by.eapp.musicroom.network

import by.eapp.musicroom.network.model.auth.LoginDataDto
import by.eapp.musicroom.network.model.auth.RegistrationDataDto
import by.eapp.musicroom.network.model.auth.SubmitDataDto
import by.eapp.musicroom.network.model.auth.TokensDto
import by.eapp.musicroom.network.model.auth.UserIdDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthorizationApiService {

    @POST(Endpoint.REGISTER)
    suspend fun registerUser(@Body registrationData: RegistrationDataDto): Response<UserIdDto>

    @POST(Endpoint.LOGIN)
    suspend fun loginUser(@Body loginData: LoginDataDto): Response<TokensDto>

    @POST(Endpoint.SEND_CODE)
    suspend fun sendCode(@Path("userId") userId: Int): Response<Void?>

    @POST(Endpoint.SUBMIT_CODE)
    suspend fun submitCode(@Body userId: SubmitDataDto): Response<String>

}

object Endpoint {
    const val REGISTER = "auth/registration"
    const val LOGIN = "auth/login"
    const val SEND_CODE = "auth/code/{userId}"
    const val SUBMIT_CODE = "auth/submit"
    const val REFRESH_TOKEN = "auth/refresh"
}