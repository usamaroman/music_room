package by.eapp.musicroom.network.model.auth

import com.google.gson.annotations.SerializedName

data class RegistrationDataDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("nickname")
    val nickname: String,
)
