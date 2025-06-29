package com.example.peliculasserieskotlin.features.shared.repository

import com.example.peliculasserieskotlin.core.database.dao.UserDao
import com.example.peliculasserieskotlin.core.database.entity.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    // Estado del usuario actual
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    suspend fun registerUser(username: String, password: String): Boolean {
        val existing = userDao.getUserByUsername(username)
        if (existing != null) return false
        val newUser = UserEntity(username = username, password = password)
        val userId = userDao.insertUser(newUser)
        // Actualizar el usuario actual con el ID generado
        _currentUser.value = newUser.copy(id = userId.toInt())
        return true
    }

    suspend fun loginUser(username: String, password: String): Boolean {
        val user = userDao.getUserByUsername(username)
        val isValid = user?.password == password
        if (isValid && user != null) {
            _currentUser.value = user
        }
        return isValid
    }

    fun enterAsGuest() {
        _currentUser.value = null
    }

    fun logout() {
        _currentUser.value = null
    }

    fun getCurrentUserId(): Int? {
        return _currentUser.value?.id
    }

    fun isUserLoggedIn(): Boolean {
        return _currentUser.value != null
    }
} 