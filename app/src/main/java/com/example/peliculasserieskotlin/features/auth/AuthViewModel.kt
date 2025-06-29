package com.example.peliculasserieskotlin.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peliculasserieskotlin.features.shared.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Exponer el usuario actual
    val currentUser = userRepository.currentUser

    fun register(username: String, password: String) {
        viewModelScope.launch {
            val success = userRepository.registerUser(username, password)
            _authState.value = if (success) AuthState.RegisterSuccess else AuthState.Error("Usuario ya existe")
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val success = userRepository.loginUser(username, password)
            _authState.value = if (success) AuthState.LoginSuccess else AuthState.Error("Credenciales incorrectas")
        }
    }

    fun enterAsGuest() {
        userRepository.enterAsGuest()
        _authState.value = AuthState.Guest
    }

    fun logout() {
        userRepository.logout()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object RegisterSuccess : AuthState()
    object LoginSuccess : AuthState()
    object Guest : AuthState()
    data class Error(val message: String) : AuthState()
} 