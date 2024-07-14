package by.eapp.musicroom.di

import by.eapp.musicroom.data.DispatcherProviderImpl
import by.eapp.musicroom.data.StatusRepo
import by.eapp.musicroom.domain.DispatcherProvider
import by.eapp.musicroom.domain.repo.StatusRepository
import by.eapp.musicroom.network.RegistrationService
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
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun provideBaseUrl(): String = "https://localhost:8080/"

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): RegistrationService =
        retrofit.create(RegistrationService::class.java)

    @Singleton
    @Provides
    fun provideStatusRepository(registrationService: RegistrationService): StatusRepository {
        return StatusRepo(registrationService)
    }

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DispatcherProviderImpl()

}