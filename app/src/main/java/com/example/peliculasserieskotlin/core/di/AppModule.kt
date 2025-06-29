package com.example.peliculasserieskotlin.core.di

import android.content.Context
import androidx.room.Room
import com.example.peliculasserieskotlin.core.database.AppDatabase
import com.example.peliculasserieskotlin.core.database.dao.FavoriteDao
import com.example.peliculasserieskotlin.core.database.dao.MediaItemDao
import com.example.peliculasserieskotlin.core.database.dao.MediaDetailDao
import com.example.peliculasserieskotlin.core.database.dao.UserDao
import com.example.peliculasserieskotlin.core.util.NetworkUtils
import com.example.peliculasserieskotlin.data.MovieApiService
import com.example.peliculasserieskotlin.data.SeriesApiService
import com.example.peliculasserieskotlin.features.shared.repository.ApiMediaRepository
import com.example.peliculasserieskotlin.features.shared.repository.FavoriteRepository
import com.example.peliculasserieskotlin.features.shared.repository.MediaRepository
import com.example.peliculasserieskotlin.features.shared.repository.RoomMediaRepository
import com.example.peliculasserieskotlin.features.shared.repository.SmartMediaRepository
import com.example.peliculasserieskotlin.features.shared.repository.UserRepository
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
        Room.databaseBuilder(ctx, AppDatabase::class.java, "media_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideMediaItemDao(db: AppDatabase): MediaItemDao = db.mediaItemDao()
    @Provides fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()
    @Provides fun provideMediaDetailDao(db: AppDatabase): MediaDetailDao = db.mediaDetailDao()
    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    /*----------------- Utils -----------------*/

    @Provides @Singleton
    fun provideNetworkUtils(@ApplicationContext ctx: Context): NetworkUtils = NetworkUtils(ctx)

    /*----------------- Repositories -----------------*/

    @Provides @Singleton
    fun provideApiMediaRepository(
        movieApi: MovieApiService,
        seriesApi: SeriesApiService,
        @ApplicationContext ctx: Context,
        roomRepository: RoomMediaRepository
    ): ApiMediaRepository = ApiMediaRepository(movieApi, seriesApi, ctx, roomRepository)

    @Provides @Singleton
    fun provideRoomMediaRepository(
        mediaItemDao: MediaItemDao, 
        favoriteDao: FavoriteDao,
        mediaDetailDao: MediaDetailDao,
        userRepository: UserRepository
    ): RoomMediaRepository = RoomMediaRepository(mediaItemDao, favoriteDao, mediaDetailDao, userRepository)

    @Provides @Singleton
    fun provideSmartMediaRepository(
        apiRepo: ApiMediaRepository,
        roomRepo: RoomMediaRepository,
        networkUtils: NetworkUtils,
        movieApiService: MovieApiService,
        seriesApiService: SeriesApiService,
        @ApplicationContext context: Context
    ): SmartMediaRepository = SmartMediaRepository(apiRepo, roomRepo, networkUtils, movieApiService, seriesApiService, context)

    @Provides @Singleton
    fun provideMediaRepository(
        smartRepo: SmartMediaRepository
    ): MediaRepository = smartRepo

    @Provides @Singleton
    fun provideFavoriteRepository(
        favoriteDao: FavoriteDao,
        mediaItemDao: MediaItemDao,
        userRepository: UserRepository
    ): FavoriteRepository =
        FavoriteRepository(favoriteDao, mediaItemDao, userRepository)

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository = UserRepository(userDao)
}
