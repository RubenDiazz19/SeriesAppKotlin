package com.example.peliculasserieskotlin.di

import android.content.Context
import androidx.room.Room
import com.example.peliculasserieskotlin.data.api.MovieApiService
import com.example.peliculasserieskotlin.data.api.SeriesApiService
import com.example.peliculasserieskotlin.data.local.AppDatabase
import com.example.peliculasserieskotlin.data.local.MediaItemDao
import com.example.peliculasserieskotlin.data.repository.ApiMediaRepository
import com.example.peliculasserieskotlin.data.repository.MediaRepository
import com.example.peliculasserieskotlin.data.repository.RoomMediaRepository
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
            .connectTimeout(60, TimeUnit.SECONDS)   // Aumentar tiempo para conectar
            .readTimeout(60, TimeUnit.SECONDS)      // Aumentar tiempo para leer la respuesta
            .writeTimeout(60, TimeUnit.SECONDS)     // Aumentar tiempo para escribir
            .retryOnConnectionFailure(true)         // Reintentar en caso de fallos
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

    // Provide AppDatabase
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "media_database"
        ).build()
    }

    // Provide MediaItemDao
    @Singleton
    @Provides
    fun provideMediaItemDao(db: AppDatabase): MediaItemDao {
        return db.mediaItemDao()
    }

    // Provide ApiMediaRepository
    @Singleton
    @Provides
    fun provideApiMediaRepository(
        movieApiService: MovieApiService,
        seriesApiService: SeriesApiService,
        @ApplicationContext context: Context
    ): ApiMediaRepository {
        return ApiMediaRepository(movieApiService, seriesApiService, context)
    }

    // Provide RoomMediaRepository with MediaItemDao
    @Singleton
    @Provides
    fun provideRoomMediaRepository(
        mediaItemDao: MediaItemDao
    ): RoomMediaRepository {
        return RoomMediaRepository(mediaItemDao)
    }

    // Provide MediaRepository
    @Singleton
    @Provides
    fun provideMediaRepository(
        apiMediaRepository: ApiMediaRepository,
        roomMediaRepository: RoomMediaRepository
    ): MediaRepository {
        // Por defecto, usamos la implementación de API
        return apiMediaRepository
    }
}
