package com.example.peliculasserieskotlin.di

import android.content.Context
import com.example.peliculasserieskotlin.data.api.MovieApiService
import com.example.peliculasserieskotlin.data.api.SeriesApiService
import com.example.peliculasserieskotlin.data.repository.ApiMovieRepository
import com.example.peliculasserieskotlin.data.repository.ApiSeriesRepository
import com.example.peliculasserieskotlin.data.repository.MovieRepository
import com.example.peliculasserieskotlin.data.repository.SeriesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.themoviedb.org/3/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)   // tiempo para conectar
            .readTimeout(30, TimeUnit.SECONDS)      // tiempo para leer la respuesta
            .writeTimeout(30, TimeUnit.SECONDS)     // tiempo para escribir si fuera POST
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }


    @Singleton
    @Provides
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideSeriesApiService(retrofit: Retrofit): SeriesApiService {
        return retrofit.create(SeriesApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideMovieRepository(
        api: MovieApiService,
        @ApplicationContext context: Context
    ): MovieRepository {
        return ApiMovieRepository(api, context)
    }

    @Singleton
    @Provides
    fun provideSeriesRepository(
        api: SeriesApiService,
        @ApplicationContext context: Context
    ): SeriesRepository {
        return ApiSeriesRepository(api, context)
    }
}
