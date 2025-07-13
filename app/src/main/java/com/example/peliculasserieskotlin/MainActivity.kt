package com.example.peliculasserieskotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.peliculasserieskotlin.features.details.SerieDetailViewModel
import com.example.peliculasserieskotlin.features.details.SerieDetailScreen
import com.example.peliculasserieskotlin.features.home.HomeScreen
import com.example.peliculasserieskotlin.features.home.HomeViewModel
import com.example.peliculasserieskotlin.ui.theme.PeliculasSeriesKotlinTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.peliculasserieskotlin.features.auth.LoginScreen
import com.example.peliculasserieskotlin.features.auth.RegisterScreen
import com.example.peliculasserieskotlin.features.auth.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val detailViewModel: SerieDetailViewModel by viewModels()
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
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val isGuest = authState is com.example.peliculasserieskotlin.features.auth.AuthState.Guest
    android.util.Log.d("DEBUG", "[AppNavigator] isGuest: $isGuest, authState: $authState")

    when (authState) {
        is com.example.peliculasserieskotlin.features.auth.AuthState.Idle,
        is com.example.peliculasserieskotlin.features.auth.AuthState.Error -> {
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
        is com.example.peliculasserieskotlin.features.auth.AuthState.LoginSuccess,
        is com.example.peliculasserieskotlin.features.auth.AuthState.RegisterSuccess,
        is com.example.peliculasserieskotlin.features.auth.AuthState.Guest -> {
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
                            viewModel = detailViewModel
                        )
                    }
                }
            }
        }
    }
}


