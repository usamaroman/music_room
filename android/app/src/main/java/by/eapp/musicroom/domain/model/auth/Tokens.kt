package by.eapp.musicroom.domain.model.auth

data class Tokens(
    val accessToken: String,
    val refreshToken: String,
)
