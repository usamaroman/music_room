package by.eapp.musicroom.network.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenDto(
    @SerializedName("token")
    val refreshToken: String,
)
