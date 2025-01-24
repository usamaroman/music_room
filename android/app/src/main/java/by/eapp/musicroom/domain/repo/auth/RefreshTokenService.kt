package by.eapp.musicroom.domain.repo.auth

import by.eapp.musicroom.domain.model.auth.Tokens

interface RefreshTokenService {
    suspend fun refreshToken(refreshToken: String): Tokens

}