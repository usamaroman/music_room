package by.eapp.musicroom.domain.repo

import by.eapp.musicroom.domain.model.Tokens

interface RefreshTokenService {
    suspend fun refreshToken(refreshToken: String): Tokens

}