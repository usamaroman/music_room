package by.eapp.musicroom.network.model.tracks

import com.google.gson.annotations.SerializedName

data class TrackDto(
    @SerializedName("artist") val artist: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("mp3") val mp3: String,
    @SerializedName("title") val title: String,
)

