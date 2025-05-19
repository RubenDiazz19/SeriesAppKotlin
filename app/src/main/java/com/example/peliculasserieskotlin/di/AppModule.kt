package com.example.peliculasserieskotlin.di

import android.content.Context
import androidx.room.Room
import com.example.peliculasserieskotlin.data.api.MovieApiService
import com.example.peliculasserieskotlin.data.api.SeriesApiService
import com.example.peliculasserieskotlin.data.local.*
import com.example.peliculasserieskotlin.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /*----------------- Retrofit -----------------*/

    private const val BASE_URL = "https://api.themoviedb.org/3/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    /*----------------- API services -----------------*/

    @Provides @Singleton
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService =
        retrofit.create(MovieApiService::class.java)

    @Provides @Singleton
    fun provideSeriesApiService(retrofit: Retrofit): SeriesApiService =
        retrofit.create(SeriesApiService::class.java)

    /*----------------- Room -----------------*/

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "media_database").build()

    @Provides fun provideMediaItemDao(db: AppDatabase): MediaItemDao = db.mediaItemDao()
    @Provides fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()

    /*----------------- Repositories -----------------*/

    @Provides @Singleton
    fun provideApiMediaRepository(
        movieApi: MovieApiService,
        seriesApi: SeriesApiService,
        @ApplicationContext ctx: Context
    ): ApiMediaRepository = ApiMediaRepository(movieApi, seriesApi, ctx)

    @Provides @Singleton
    fun provideRoomMediaRepository(mediaItemDao: MediaItemDao): RoomMediaRepository =
        RoomMediaRepository(mediaItemDao)

    @Provides @Singleton
    fun provideMediaRepository(
        apiRepo: ApiMediaRepository,
        roomRepo: RoomMediaRepository
    ): MediaRepository = apiRepo

    @Provides @Singleton
    fun provideFavoriteRepository(
        favoriteDao: FavoriteDao,
        mediaItemDao: MediaItemDao
    ): FavoriteRepository =
        FavoriteRepository(favoriteDao, mediaItemDao)
}
