package com.nhathuy.gas24h_7app.data.repository

import com.nhathuy.gas24h_7app.data.model.User

interface UserRepository {
    suspend fun registerUser(user: User):Result<Unit>
    suspend fun getUser(userId:String):Result<User>
    suspend fun updateUser(user:User):Result<Unit>
    fun getCurrentUserId(): String?
    fun isUserLoggedIn(): Boolean

    fun logout()
}