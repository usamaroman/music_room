package by.eapp.musicroom.di

import by.eapp.musicroom.data.login.repo.AuthAuthenticator
import by.eapp.musicroom.data.login.repo.AuthorizationServiceImpl
import by.eapp.musicroom.data.login.repo.RefreshTokenServiceImpl
import by.eapp.musicroom.domain.repo.login.AuthorizationService
import by.eapp.musicroom.domain.repo.login.JwtTokenManager
import by.eapp.musicroom.network.AuthorizationApiService
import by.eapp.musicroom.network.RefreshTokenApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object AppModule {


    @[Provides Singleton]
    fun provideAuthAuthenticator(
        tokenManager: JwtTokenManager,
        apiService: RefreshTokenServiceImpl,
    ): AuthAuthenticator = AuthAuthenticator(
        tokenManager = tokenManager,
        apiService = apiService
    )

    @[Provides Singleton]
    fun provideRefreshTokenService(
        apiService: RefreshTokenApiService,
    ) = RefreshTokenServiceImpl(apiService = apiService)

    @[Provides Singleton]
    fun provideAuthorizationService(
        tokenManager: JwtTokenManager,
        apiService: AuthorizationApiService,
    ): AuthorizationService = AuthorizationServiceImpl(
        tokenManager = tokenManager,
        apiService = apiService
    )
}





