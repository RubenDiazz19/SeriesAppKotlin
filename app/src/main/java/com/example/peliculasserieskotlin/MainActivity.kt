package com.example.peliculasserieskotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.peliculasserieskotlin.features.home.HomeViewModel
import com.example.peliculasserieskotlin.features.home.HomeScreen
import com.example.peliculasserieskotlin.ui.theme.PeliculasSeriesKotlinTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar el manejador de navegación hacia atrás
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Si el buscador inline está activo, ciérralo
                // HomeScreen.kt's BackHandler should primarily handle this.
                // This acts as a fallback or if HomeScreen is not in the foreground of back handling.
                if (homeViewModel.inlineSearchActive.value) {
                    homeViewModel.hideInlineSearch()
                } else {
                    // Comportamiento normal de retroceso
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })


        setContent {
            PeliculasSeriesKotlinTheme {
                HomeScreen(viewModel = homeViewModel)
            }
        }
    }
}