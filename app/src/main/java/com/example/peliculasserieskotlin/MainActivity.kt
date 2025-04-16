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
import com.example.peliculasserieskotlin.presentation.home.HomeScreen
import com.example.peliculasserieskotlin.presentation.home.HomeViewModel
import com.example.peliculasserieskotlin.ui.theme.PeliculasSeriesKotlinTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeliculasSeriesKotlinTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onMovieClick = { movie ->
                                navController.navigate("detail/${movie.id}")
                            }
                        )
                    }
                    composable("detail/{movieId}") { backStackEntry ->
                        val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                        val uiState = viewModel.uiState.collectAsState().value
                        val movie = uiState.movies.find { it.id == movieId }

                        movie?.let {
                            MovieDetailScreen(movie = it)
                        }
                    }

                }
            }
        }
    }
}