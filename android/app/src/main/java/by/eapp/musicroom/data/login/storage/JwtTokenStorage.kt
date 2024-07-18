package by.eapp.musicroom.data.login.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import by.eapp.musicroom.domain.repo.login.JwtTokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JwtTokenStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : JwtTokenManager {
    override suspend fun saveAccessJwt(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_JWT_KEY] = token
        }
    }

    override suspend fun saveRefreshJwt(token: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_JWT_KEY] = token
        }
    }

    override suspend fun getAccessJwt(): String? {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_JWT_KEY]
        }.first()
    }

    override suspend fun getRefreshJwt(): String? {
        return dataStore.data.map { preferences ->
            preferences[REFRESH_JWT_KEY]
        }.first()
    }

    override suspend fun clearAllTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_JWT_KEY)
            preferences.remove(REFRESH_JWT_KEY)
        }
    }

    companion object {
        val ACCESS_JWT_KEY = stringPreferencesKey("access_jwt")
        val REFRESH_JWT_KEY = stringPreferencesKey("refresh_jwt")
    }
}