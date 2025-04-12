package com.example.peliculasserieskotlin.di

import com.example.peliculasserieskotlin.data.FakeMovieRepository
import com.example.peliculasserieskotlin.domain.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        impl: FakeMovieRepository
    ): MovieRepository
}
