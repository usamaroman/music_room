package by.eapp.musicroom.domain.repo

interface StatusRepository {
    suspend fun getStatus(): String
}