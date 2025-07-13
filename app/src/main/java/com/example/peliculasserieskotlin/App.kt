package com.example.peliculasserieskotlin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    
    @Inject
    lateinit var roomSerieRepository: com.example.peliculasserieskotlin.features.shared.repository.RoomSerieRepository
    
    override fun onCreate() {
        super.onCreate()
        // Limpiar la caché excepto favoritos al iniciar
        CoroutineScope(Dispatchers.IO).launch {
            roomSerieRepository.clearCacheExceptFavorites()
        }
    }
}
