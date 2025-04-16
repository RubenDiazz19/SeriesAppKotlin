package com.example.peliculasserieskotlin.di

import android.content.Context
import androidx.room.Room
import com.example.peliculasserieskotlin.data.*
import com.example.peliculasserieskotlin.domain.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MovieDatabase {
        return Room.databaseBuilder(
            appContext,
            MovieDatabase::class.java,
            "movies_db"
        ).build()
    }


    @Provides
    fun provideMovieDao(database: MovieDatabase): MovieDao {
        return database.movieDao()
    }
    /*
    @Provides
    @Singleton
    fun provideMovieRepository(movieDao: MovieDao): MovieRepository {
        return RoomMovieRepository(movieDao)
    }
    */

}
