package com.example.seriesappkotlin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    
    @Inject
    lateinit var roomSerieRepository: com.example.seriesappkotlin.features.shared.repository.RoomSerieRepository
    
    override fun onCreate() {
        super.onCreate()
        // Si necesitas limpiar caché, implementa el método en RoomSerieRepository
        // Actualmente no existe clearCacheExceptFavorites
    }
}
