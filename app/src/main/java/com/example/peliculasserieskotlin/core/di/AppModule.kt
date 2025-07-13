package com.example.peliculasserieskotlin.core.di

import android.content.Context
import androidx.room.Room
import com.example.peliculasserieskotlin.core.database.AppDatabase
import com.example.peliculasserieskotlin.core.database.dao.FavoriteDao
import com.example.peliculasserieskotlin.core.database.dao.SerieDao
import com.example.peliculasserieskotlin.core.database.dao.UserDao
import com.example.peliculasserieskotlin.core.util.NetworkUtils
import com.example.peliculasserieskotlin.data.SeriesApiService
import com.example.peliculasserieskotlin.features.shared.repository.ApiSerieRepository
import com.example.peliculasserieskotlin.features.shared.repository.FavoriteRepository
import com.example.peliculasserieskotlin.features.shared.repository.RoomSerieRepository
import com.example.peliculasserieskotlin.features.shared.repository.SerieRepository
import com.example.peliculasserieskotlin.features.shared.repository.SmartSerieRepository
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
    fun provideSeriesApiService(retrofit: Retrofit): SeriesApiService =
        retrofit.create(SeriesApiService::class.java)

    /*----------------- Room -----------------*/

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "media_database")
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()

    @Provides fun provideSerieDao(db: AppDatabase): SerieDao = db.serieDao()
    @Provides fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()
    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    //todo
    // @Provides fun provideSerieDetailDao(db: AppDatabase): SerieDetailDao = db.serieDetailDao()

    /*----------------- Utils -----------------*/

    @Provides @Singleton
    fun provideNetworkUtils(@ApplicationContext ctx: Context): NetworkUtils = NetworkUtils(ctx)

    /*----------------- Repositories -----------------*/

    @Provides
    @Singleton
    fun provideApiSerieRepository(
        seriesApi: SeriesApiService,
        @ApplicationContext ctx: Context,
        roomRepo: RoomSerieRepository
    ): ApiSerieRepository = ApiSerieRepository(
        seriesApi, ctx,
        roomRepository = roomRepo
    )

    @Provides
    @Singleton
    fun provideRoomSerieRepository(
        serieDao: SerieDao,
        favoriteDao: FavoriteDao,
        userRepository: UserRepository
    ): RoomSerieRepository = RoomSerieRepository(serieDao, favoriteDao, userRepository)

    @Provides
    @Singleton
    fun provideSerieRepository(
        apiRepo: ApiSerieRepository,
        roomRepo: RoomSerieRepository,
        networkUtils: NetworkUtils,
        seriesApiService: SeriesApiService,
        @ApplicationContext context: Context
    ): SerieRepository = SmartSerieRepository(
        apiRepo,
        roomRepo,
        networkUtils,
        seriesApiService,
        context
    )

    @Provides @Singleton
    fun provideFavoriteRepository(
        favoriteDao: FavoriteDao,
        serieDao: SerieDao,
        userRepository: UserRepository
    ): FavoriteRepository =
        FavoriteRepository(favoriteDao, serieDao, userRepository)

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository = UserRepository(userDao)
}
