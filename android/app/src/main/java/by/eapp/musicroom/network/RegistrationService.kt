package by.eapp.musicroom.network

import retrofit2.Response
import retrofit2.http.GET

interface RegistrationService {
    @GET("status")
    fun getStatus(): Response<String>
}