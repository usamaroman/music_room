package by.eapp.musicroom.network.model

import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("status")
    val status: String
)
