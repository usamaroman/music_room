package by.eapp.musicroom.network.model.auth

import com.google.gson.annotations.SerializedName

data class RefreshTokenDto(
    @SerializedName("token")
    val refreshToken: String,
)
