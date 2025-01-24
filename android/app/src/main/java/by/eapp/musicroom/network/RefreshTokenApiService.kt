package by.eapp.musicroom.network

import by.eapp.musicroom.network.model.auth.RefreshTokenDto
import by.eapp.musicroom.network.model.auth.TokensDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshTokenApiService {

    @POST(Endpoint.REFRESH_TOKEN)
    suspend fun refreshToken(@Body refreshToken: RefreshTokenDto): Response<TokensDto>
}