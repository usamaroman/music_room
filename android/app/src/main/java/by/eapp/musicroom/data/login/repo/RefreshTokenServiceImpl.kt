package by.eapp.musicroom.data.login.repo

import by.eapp.musicroom.data.toDto
import by.eapp.musicroom.domain.model.RefreshToken
import by.eapp.musicroom.domain.model.Tokens
import by.eapp.musicroom.domain.repo.RefreshTokenService
import by.eapp.musicroom.network.RefreshTokenApiService
import javax.inject.Inject

class RefreshTokenServiceImpl @Inject constructor(
    private val apiService: RefreshTokenApiService,
) : RefreshTokenService {
    override suspend fun refreshToken(refreshToken: String): Tokens {
        val response = apiService.refreshToken(RefreshToken(refreshToken).toDto())
        if (response.isSuccessful) {
            val result = response.body()
            if (result != null) {
                return Tokens(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken
                )
            } else {
                throw Exception("Response body is null")
            }
        } else {
            throw Exception("Failed to refresh token: ${response.errorBody()?.string()}")
        }
    }
}