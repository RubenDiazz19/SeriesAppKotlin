package com.example.peliculasserieskotlin.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val year: String,
    val posterUrl: String,
    val overview: String,
    val voteAverage: Double
)
