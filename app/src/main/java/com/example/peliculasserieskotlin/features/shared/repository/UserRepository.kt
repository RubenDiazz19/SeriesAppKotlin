package com.example.peliculasserieskotlin.features.shared.repository

import com.example.peliculasserieskotlin.core.database.dao.UserDao
import com.example.peliculasserieskotlin.core.database.entity.UserEntity
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun registerUser(username: String, password: String): Boolean {
        val existing = userDao.getUserByUsername(username)
        if (existing != null) return false
        userDao.insertUser(UserEntity(username = username, password = password))
        return true
    }

    suspend fun loginUser(username: String, password: String): Boolean {
        val user = userDao.getUserByUsername(username)
        return user?.password == password
    }
} 