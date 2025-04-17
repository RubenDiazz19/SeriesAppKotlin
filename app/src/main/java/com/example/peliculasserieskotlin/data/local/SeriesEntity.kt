package com.example.peliculasserieskotlin.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series_table")
data class SeriesEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val year: String,
    val overview: String,
    val posterUrl: String,
    val voteAverage: Double
)

