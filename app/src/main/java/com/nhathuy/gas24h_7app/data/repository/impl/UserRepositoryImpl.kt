package com.nhathuy.gas24h_7app.data.repository.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth:FirebaseAuth,
    private val db:FirebaseFirestore):UserRepository {

    private var currentUser: User? = null

    override suspend fun registerUser(user: User): Result<Unit>  = withContext(Dispatchers.IO) {
        try {
            val userId= auth.currentUser?.uid?:""
            db.collection("users").document(userId).set(user.toMap()).await()
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

    override suspend fun getCurrentUser(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: return@withContext Result.failure(Exception("User not logged in"))
            val userDoc = db.collection("users").document(userId).get().await()
            val user = User.formDocumentSnapshot(userDoc)

            user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("User data not found"))
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

    override suspend fun getUserAdminId(): Result<String> = withContext(Dispatchers.IO){
        try {

            val snapshot= db.collection("users")
                .whereEqualTo("phoneNumber" ,Constants.ADMIN_PHONE_NUMBER)
                .get()
                .await()

            val userAdminId = if(!snapshot.isEmpty){
                snapshot.documents.first().id
            }
            else{
                return@withContext Result.failure(Exception("No user found with this phone number"))
            }
            Result.success(userAdminId)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    // check is userAdmin
    override suspend fun isUserAdmin(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val currentUser = auth.currentUser ?: return@withContext Result.failure(Exception("No authenticated user"))

            val userDoc = db.collection("users").document(currentUser.uid)
                .get()
                .await()

            val user = userDoc.toObject(User::class.java)

            if(user?.phoneNumber == Constants.ADMIN_PHONE_NUMBER){
                Result.success(true)
            }
            else{
                Result.success(false)
            }
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    // add this function to create admin user if it doesn't exist
    override suspend fun createAdminUser(phoneNumber: String) :Result<Unit>  = withContext(Dispatchers.IO) {
        try {
            val currentUser = auth.currentUser ?:return@withContext Result.failure(Exception("No authenticated user"))

            val user = User(uid = currentUser.uid,
                phoneNumber = phoneNumber,
                fullName = "Admin",
                isAdmin = true
            )

            db.collection("users")
                .document(currentUser.uid)
                .set(user)
                .await()

            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(): Result<Unit> = withContext(Dispatchers.IO){
        try {
            val currentUser = auth.currentUser
                ?: return@withContext Result.failure(Exception("No authenticated user found"))

            if (isUserAdmin().getOrNull() == true) {
                return@withContext Result.failure(Exception("Không thể xóa tài khoản admin"))
            }

            val hasPendingOrders = checkPendingOrders(currentUser.uid)
            if (hasPendingOrders) {
                return@withContext Result.failure(Exception("Không thể xóa tài khoản khi còn đơn hàng đang xử lý"))
            }

            deleteUserRelatedData(currentUser.uid)

            // Delete user document from Firestore
            db.collection("users").document(currentUser.uid).delete().await()

            // Delete user from Firebase Authentication
            currentUser.delete().await()

            // Clear the current user
            auth.signOut()

            // Reset the local currentUser
            this@UserRepositoryImpl.currentUser = null

            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    private suspend fun checkPendingOrders(uid: String): Boolean {
        return try {
            // Danh sách các trạng thái đơn hàng đang xử lý
            val pendingStatuses = listOf(
                OrderStatus.PENDING.name,
                OrderStatus.DELIVERED.name,
                OrderStatus.SHIPPED.name
            )

            // Kiểm tra các đơn hàng của người dùng
            val snapshot = db.collection("orders")
                .whereEqualTo("userId", uid)
                .whereIn("status", pendingStatuses)
                .get()
                .await()

            // Trả về true nếu có đơn hàng đang xử lý
            !snapshot.isEmpty

        } catch (e: Exception) {
            // Nếu có lỗi, giả định là có đơn hàng đang xử lý để đảm bảo an toàn
            true
        }
    }

    private suspend fun deleteUserRelatedData(userId: String) {
        // Delete orders
        db.collection("orders")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete().await() }

        // Delete cart items
        db.collection("carts")
            .document(userId)
            .delete()
            .await()
    }
    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser!=null
    }

    override fun logout() {
        auth.signOut()
        currentUser=null
    }


}