package by.eapp.musicroom.network.model.auth

import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("status")
    val status: String
)
