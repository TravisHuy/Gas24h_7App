package com.nhathuy.gas24h_7app.data.repository.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth:FirebaseAuth,
    private val db:FirebaseFirestore):UserRepository {


    override suspend fun registerUser(user: User): Result<Unit>  = withContext(Dispatchers.IO) {
        try {
            val currentUser= auth.currentUser?: return@withContext Result.failure(Exception("No authenticated user found"))
            db.collection("users").document(currentUser.uid).set(user.toMap()).await()
            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("users").document(userId).get().await()
            val user = snapshot.toObject(User::class.java)
                ?: return@withContext Result.failure(Exception("User not found"))
            Result.success(user)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> = withContext(Dispatchers.IO){
        try {
            val currentUser = auth.currentUser
                ?: return@withContext Result.failure(Exception("No authenticated user found"))

            db.collection("users").document(currentUser.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}