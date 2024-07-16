package by.eapp.musicroom.data

import by.eapp.musicroom.domain.model.LoginData
import by.eapp.musicroom.domain.model.RefreshToken
import by.eapp.musicroom.domain.model.RegistrationData
import by.eapp.musicroom.domain.model.SubmitData
import by.eapp.musicroom.domain.model.Tokens
import by.eapp.musicroom.network.model.LoginDataDto
import by.eapp.musicroom.network.model.RefreshTokenDto
import by.eapp.musicroom.network.model.RegistrationDataDto
import by.eapp.musicroom.network.model.SubmitDataDto
import by.eapp.musicroom.network.model.TokensDto

//Registration
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





