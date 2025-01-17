package by.eapp.musicroom.di

import by.eapp.musicroom.Const
import by.eapp.musicroom.network.AuthorizationApiService
import by.eapp.musicroom.network.RefreshTokenApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @[Provides Singleton]
    fun provideBaseUrl(): String = "https://localhost:8080/"

    @[Provides Singleton]
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    @[Provides Singleton]
    fun provideAuthenticationApi(@Clients.AuthenticatedClient okHttpClient: OkHttpClient): AuthorizationApiService {
        return Retrofit.Builder()
            .baseUrl(Const.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(AuthorizationApiService::class.java)
    }

    @[Provides Singleton]
    fun provideRefreshTokenServiceApi(@Clients.TokenRefreshClient okHttpClient: OkHttpClient): RefreshTokenApiService {
        return Retrofit.Builder()
            .baseUrl(Const.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RefreshTokenApiService::class.java)
    }

}