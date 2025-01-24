package by.eapp.musicroom.network.model.auth

import com.google.gson.annotations.SerializedName

data class UserIdDto(
    @SerializedName("id")
    val userId: Int,
)
