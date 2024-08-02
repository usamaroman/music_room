package by.eapp.musicroom.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import by.eapp.musicroom.data.login.storage.JwtTokenStorage
import by.eapp.musicroom.domain.repo.login.JwtTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @[Provides Singleton]
    fun provideDataStore(
        @ApplicationContext appContext: Context,
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { appContext.preferencesDataStoreFile("auth_datastore") }
        )
    }


    @[Provides Singleton]
    fun provideJwtTokenManager(dataStore: DataStore<Preferences>):
            JwtTokenManager = JwtTokenStorage(dataStore)


}