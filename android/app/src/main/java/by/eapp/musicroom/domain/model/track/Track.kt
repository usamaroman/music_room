package by.eapp.musicroom.domain.model.track

import java.io.File

data class Track(
    val id: Int,
    val title: String,
    val artist: String,
    val trackURI: String,
    val image: String,
    val createdAt: String
)

