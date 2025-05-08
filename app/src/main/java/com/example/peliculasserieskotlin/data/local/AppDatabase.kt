package com.example.peliculasserieskotlin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.peliculasserieskotlin.data.local.MediaItemEntity
import com.example.peliculasserieskotlin.domain.model.MediaItem

@Database(entities = [MediaItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaItemDao(): MediaItemDao
}

