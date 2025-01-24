package by.eapp.musicroom.network.model.auth

import com.google.gson.annotations.SerializedName

data class TokensDto(
    @SerializedName("access_token")
    val accessToken: String = "",
    @SerializedName("refresh_token")
    val refreshToken: String = "",
)
