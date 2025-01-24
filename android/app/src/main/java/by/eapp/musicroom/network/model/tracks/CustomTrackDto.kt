package by.eapp.musicroom.network.model.tracks

import com.google.gson.annotations.SerializedName

data class CustomTrackDto(
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("track") val track: String,
    @SerializedName("image") val image: String,
)
