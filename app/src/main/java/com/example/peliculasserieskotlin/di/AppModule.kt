package com.example.peliculasserieskotlin.di

import android.app.Application
import androidx.room.Room
import com.example.peliculasserieskotlin.data.api.MovieApiService
import com.example.peliculasserieskotlin.data.api.SeriesApiService
import com.example.peliculasserieskotlin.data.local.AppDatabase
import com.example.peliculasserieskotlin.data.repository.*
import com.example.peliculasserieskotlin.data.repository.MovieRepository
import com.example.peliculasserieskotlin.data.repository.SeriesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.themoviedb.org/3/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieApi(retrofit: Retrofit): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSeriesApi(retrofit: Retrofit): SeriesApiService {
        return retrofit.create(SeriesApiService::class.java)
    }

    // Room
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "media_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideMovieDao(db: AppDatabase) = db.movieDao()

    @Provides
    fun provideSeriesDao(db: AppDatabase) = db.seriesDao()

    // Repositories
    @Provides
    @Singleton
    fun provideMovieRepository(api: MovieApiService): MovieRepository {
        return ApiMovieRepository(api)
    }

    // Corrección clave aquí: Asegurarse de devolver el tipo correcto (SeriesRepository)
    @Provides
    @Singleton
    fun provideSeriesRepository(api: SeriesApiService): SeriesRepository {
        return ApiSeriesRepository(api)
    }
}
