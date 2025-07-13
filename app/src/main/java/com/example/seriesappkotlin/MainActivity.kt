package com.example.seriesappkotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.seriesappkotlin.features.auth.AuthState
import com.example.seriesappkotlin.features.details.serie.SerieDetailViewModel
import com.example.seriesappkotlin.features.details.serie.SerieDetailScreen
import com.example.seriesappkotlin.features.home.HomeScreen
import com.example.seriesappkotlin.features.home.HomeViewModel
import com.example.seriesappkotlin.features.details.season.SeasonDetailScreen
import com.example.seriesappkotlin.features.details.season.SeasonDetailViewModel
import com.example.seriesappkotlin.ui.theme.PeliculasSeriesKotlinTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.seriesappkotlin.features.auth.LoginScreen
import com.example.seriesappkotlin.features.auth.RegisterScreen
import com.example.seriesappkotlin.features.auth.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val detailViewModel: SerieDetailViewModel by viewModels()
    private val seasonDetailViewModel: SeasonDetailViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PeliculasSeriesKotlinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator(
                        homeViewModel = homeViewModel,
                        detailViewModel = detailViewModel,
                        seasonDetailViewModel = seasonDetailViewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigator(
    homeViewModel: HomeViewModel,
    detailViewModel: SerieDetailViewModel,
    seasonDetailViewModel: SeasonDetailViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val isGuest = authState is AuthState.Guest
    Log.d("DEBUG", "[AppNavigator] isGuest: $isGuest, authState: $authState")

    when (authState) {
        is AuthState.Idle,
        is AuthState.Error -> {
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = { /* No navegues aquí, la navegación depende de authState */ },
                        onNavigateToRegister = { navController.navigate("register") },
                        viewModel = authViewModel
                    )
                }
                composable("register") {
                    RegisterScreen(
                        onRegisterSuccess = { /* No navegues aquí, la navegación depende de authState */ },
                        onNavigateToLogin = { navController.popBackStack() },
                        viewModel = authViewModel
                    )
                }
            }
        }
        is AuthState.LoginSuccess,
        is AuthState.RegisterSuccess,
        is AuthState.Guest -> {
            // Solo aquí se permite cargar HomeScreen y los datos
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToDetail = { id ->
                            navController.navigate("detail/$id")
                        }
                    )
                }
                composable(
                    route = "detail/{id}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val id = backStackEntry.arguments!!.getInt("id")
                    detailViewModel.loadDetail(id)
                    val detailState by detailViewModel.uiState.collectAsState()
                    if (detailState != null) {
                        SerieDetailScreen(
                            serieId = id,
                            onBackClick = { navController.popBackStack() },
                            viewModel = detailViewModel,
                            onSeasonClick = { serieId, seasonNumber ->
                                navController.navigate("seasonDetail/$serieId/$seasonNumber")
                            }
                        )
                    }
                }
                composable(
                    route = "seasonDetail/{serieId}/{seasonNumber}",
                    arguments = listOf(
                        navArgument("serieId") { type = NavType.IntType },
                        navArgument("seasonNumber") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val serieId = backStackEntry.arguments!!.getInt("serieId")
                    val seasonNumber = backStackEntry.arguments!!.getInt("seasonNumber")
                    seasonDetailViewModel.loadSeasonDetails(serieId, seasonNumber)

                    SeasonDetailScreen(
                        viewModel = seasonDetailViewModel,
                        onBackClick = { navController.popBackStack() },
                        serieId = serieId,
                        seasonNumber = seasonNumber
                    )
                }
            }
        }
    }
}


