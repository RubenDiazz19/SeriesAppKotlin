package com.example.peliculasserieskotlin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.lifecycle.lifecycleScope
import com.example.peliculasserieskotlin.core.cache.AppCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Limpiar la caché excepto favoritos al iniciar
        CoroutineScope(Dispatchers.IO).launch {
            val appCache = AppCache(this@App)
            appCache.clearCacheExceptFavorites()
        }
    }
}
