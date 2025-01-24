package by.eapp.musicroom.data

import by.eapp.musicroom.domain.model.auth.LoginData
import by.eapp.musicroom.domain.model.auth.RefreshToken
import by.eapp.musicroom.domain.model.auth.RegistrationData
import by.eapp.musicroom.domain.model.auth.SubmitData
import by.eapp.musicroom.domain.model.auth.Tokens
import by.eapp.musicroom.domain.model.track.Track
import by.eapp.musicroom.network.model.auth.LoginDataDto
import by.eapp.musicroom.network.model.auth.RefreshTokenDto
import by.eapp.musicroom.network.model.auth.RegistrationDataDto
import by.eapp.musicroom.network.model.auth.SubmitDataDto
import by.eapp.musicroom.network.model.auth.TokensDto
import by.eapp.musicroom.network.model.tracks.TrackDto

//Registration
object RegistrationMappers {
    fun RegistrationData.toDto() = RegistrationDataDto(
        email = email,
        password = password,
        nickname = nickname
    )

    fun LoginData.toDto() = LoginDataDto(
        email = email,
        password = password
    )

    fun SubmitData.toDto() = SubmitDataDto(
        userId = userId,
        code = code
    )

    fun TokensDto.toDomain() = Tokens(
        accessToken = accessToken,
        refreshToken = refreshToken
    )

    fun RefreshToken.toDto() = RefreshTokenDto(
        refreshToken = token
    )
}

object TrackMappers {
    fun TrackDto.toDomain() = Track(
        id = id,
        title = title,
        artist = artist,
        trackURI = mp3,
        image = cover,
        createdAt = createdAt,
    )
}




