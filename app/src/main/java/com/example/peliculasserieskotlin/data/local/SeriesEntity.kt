package com.example.peliculasserieskotlin.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "series",
    indices = [Index(value = ["name"]), Index(value = ["voteAverage"])]
)
data class SeriesEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val posterUrl: String,
    val year: String,
    val overview: String,
    val voteAverage: Double
)