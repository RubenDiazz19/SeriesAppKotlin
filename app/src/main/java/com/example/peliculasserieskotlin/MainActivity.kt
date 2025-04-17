package com.example.peliculasserieskotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.peliculasserieskotlin.presentation.detail.MovieDetailScreen
import com.example.peliculasserieskotlin.presentation.detail.SeriesDetailScreen
import com.example.peliculasserieskotlin.presentation.home.HomeScreen
import com.example.peliculasserieskotlin.presentation.home.HomeViewModel
import com.example.peliculasserieskotlin.ui.theme.PeliculasSeriesKotlinTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeliculasSeriesKotlinTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onSeriesClick = { series ->
                                navController.navigate("detail/${series.id}")
                            }
                        )
                    }
                    composable("detail/{seriesId}") { backStackEntry ->
                        val seriesId = backStackEntry.arguments?.getString("seriesId")?.toIntOrNull()
                        val uiState = viewModel.uiState.collectAsState().value
                        val series = uiState.series.find { it.id == seriesId }

                        series?.let {
                            SeriesDetailScreen(series = it)
                        }
                    }
                }
            }
        }
    }
}


