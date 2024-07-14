package by.eapp.musicroom.data

import android.util.Log
import by.eapp.musicroom.domain.repo.StatusRepository
import by.eapp.musicroom.network.RegistrationService

class StatusRepo(
    private val registrationService: RegistrationService
): StatusRepository {
    override suspend fun getStatus(): String {
        try {
            val response = registrationService.getStatus()
            if (response.isSuccessful) {
                val status = response.body()
                return status.toString()
            } else {
                return "Error"
            }
        } catch (e: Exception) {
            Log.d("-------------------------------------------------------------", e.toString())
            return "Error"
        }

    }
}