package by.eapp.musicroom.network.model.auth

import com.google.gson.annotations.SerializedName

data class SubmitDataDto(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("code")
    val code: String,
)
