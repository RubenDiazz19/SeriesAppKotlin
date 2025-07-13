package com.example.seriesappkotlin.core.di

import android.content.Context
import androidx.room.Room
import com.example.seriesappkotlin.core.database.AppDatabase
import com.example.seriesappkotlin.core.database.dao.FavoriteDao
import com.example.seriesappkotlin.core.database.dao.SerieDao
import com.example.seriesappkotlin.core.database.dao.UserDao
import com.example.seriesappkotlin.core.database.dao.WatchedDao
import com.example.seriesappkotlin.core.util.NetworkUtils
import com.example.seriesappkotlin.data.SeriesApiService
import com.example.seriesappkotlin.features.shared.repository.ApiSerieRepository
import com.example.seriesappkotlin.features.shared.repository.RoomSerieRepository
import com.example.seriesappkotlin.features.shared.repository.SerieRepository
import com.example.seriesappkotlin.features.shared.repository.SmartSerieRepository
import com.example.seriesappkotlin.features.shared.repository.UserRepository
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "media_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSerieDao(appDatabase: AppDatabase): SerieDao {
        return appDatabase.serieDao()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    fun provideWatchedDao(appDatabase: AppDatabase): WatchedDao {
        return appDatabase.watchedDao()
    }

    @Provides
    fun provideFavoriteDao(appDatabase: AppDatabase): FavoriteDao {
        return appDatabase.favoriteDao()
    }

    @Provides
    fun provideSerieRepository(
        apiSerieRepository: ApiSerieRepository,
        roomSerieRepository: RoomSerieRepository,
        networkUtils: NetworkUtils,
        seriesApiService: SeriesApiService,
        @ApplicationContext context: Context
    ): SerieRepository {
        return SmartSerieRepository(
            apiSerieRepository,
            roomSerieRepository,
            networkUtils = networkUtils,
            seriesApiService = seriesApiService,
            context = context
        )
    }

    @Provides
    @Singleton
    fun provideSmartSerieRepository(
        apiSerieRepository: ApiSerieRepository,
        roomSerieRepository: RoomSerieRepository,
        networkUtils: NetworkUtils,
        seriesApiService: SeriesApiService,
        @ApplicationContext context: Context
    ): SmartSerieRepository {
        return SmartSerieRepository(apiSerieRepository, roomSerieRepository, networkUtils, seriesApiService, context)
    }
}
