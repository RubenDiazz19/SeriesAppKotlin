package com.example.peliculasserieskotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.peliculasserieskotlin.presentation.home.HomeViewModel
import com.example.peliculasserieskotlin.presentation.home.HomeScreen
import com.example.peliculasserieskotlin.ui.theme.PeliculasSeriesKotlinTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeliculasSeriesKotlinTheme {
                HomeScreen(viewModel = homeViewModel)
            }
        }
    }
}
