package com.nhathuy.gas24h_7app.fragment.profile

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.CartRepository
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ProfilePresenter @Inject constructor(private val storage: FirebaseStorage,
                                           private val userRepository: UserRepository,
                                           private val orderRepository: OrderRepository,
                                           private val productRepository: ProductRepository,
                                           private val cartRepository: CartRepository
):ProfileContract.Presenter {

    private var view:ProfileContract.View? =null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    override fun attachView(view: ProfileContract.View) {
        this.view=view
        loadUserInfo()
    }

    override fun detachView() {
        view = null
    }

    override fun updateProfileImage(imageUri: Uri) {
       coroutineScope.launch {
           try {
                val imageUrl = updateImageToFirebaseStorage(imageUri)
               val currentUser= userRepository.getCurrentUser().getOrThrow()

               currentUser.imageUser = imageUrl

               userRepository.updateUser(currentUser).getOrThrow()

               view?.showUpdateProfileImage(imageUrl)

               view?.showMessage("Profile image update successfully")
           }
           catch (e:Exception){
               view?.showError("Failed to update profile image: ${e.message}")
           }
       }
    }

    override fun loadUserInfo() {
        coroutineScope.launch {
            try {
                val result = userRepository.getUser(userRepository.getCurrentUserId()!!)
                result.fold(
                    onSuccess = { user ->
                        view?.updateProfileImage(user.imageUser?:"")
                        view?.updateUserName(user.fullName ?:"")
                        view?.showUserInfo(true)
                    },
                    onFailure = {
                        view?.showUserInfo(false)
                        view?.showError("Failed to load user profile")
                    }
                )
            }
            catch (e:Exception){
                view?.showError("Failed to load user information ${e.message}")
            }
        }
    }

    override fun loadOrders(status: String) {
        coroutineScope.launch {

            try {
                val userId = userRepository.getCurrentUserId()

                val result = orderRepository.getOrdersForUser(userId!!, status)
                result.fold(
                    onSuccess = { orders ->
                        val productIds =
                            orders.flatMap { it.items.map { item -> item.productId } }.distinct()
                        val productMap = mutableMapOf<String, Product>()
                        val productJobs = productIds.map { productId ->
                            async(Dispatchers.IO) {
                                val productResult = productRepository.getProductById(productId)
                                productResult.getOrNull()?.let { product ->
                                    productMap[productId] = product
                                }
                            }
                        }
                        productJobs.forEach { it.await() }
                        view?.showOrders(orders, productMap)
                    },
                    onFailure = { e ->
                        view?.showError("Failed to load order: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                view?.showError("Failed to load order: ${e.message}")
            } finally {
            }

        }
    }

    override fun loadCartItemCount() {
        coroutineScope.launch {
            try {
                val userId = userRepository.getCurrentUserId()
                if(userId!=null){
                    val result = cartRepository.getCartItemCount(userId)
                    result.fold(
                        onSuccess = { count ->
                            view?.updateCartItemCount(count)
                        },
                        onFailure = { e->
                            view?.showError(e.message?:"Failed to load cart item count")
                        }
                    )
                }
            }
            catch (e:Exception){
                view?.showError(e.message?:"Failed to load cart item count")
            }
        }
    }

    override fun loadOrderCount() {
        coroutineScope.launch {
            try {
                val userId = userRepository.getCurrentUserId()

                if(userId!=null){
                    val pendingCount = orderRepository.getOrderCountForUser(userId,OrderStatus.PENDING.name).getOrDefault(0)
                    val processingCount = orderRepository.getOrderCountForUser(userId,OrderStatus.PROCESSING.name).getOrDefault(0)
                    val shippedCount = orderRepository.getOrderCountForUser(userId, OrderStatus.SHIPPED.name).getOrDefault(0)
                    val deliveredCount = orderRepository.getOrderCountForUser(userId, OrderStatus.DELIVERED.name).getOrDefault(0)


                    view?.updateOrderCount( processingCount,pendingCount, shippedCount, deliveredCount)
                }
            }
            catch (e:Exception){
                view?.showError(e.message?:"Failed to load order counts")
            }
        }
    }

    private suspend fun updateImageToFirebaseStorage(imageUri: Uri): String = withContext(Dispatchers.IO) {
        val filename = UUID.randomUUID().toString()
        val ref = storage.reference.child("profile_images/$filename")

        val uploadTask = ref.putFile(imageUri).await()
        return@withContext ref.downloadUrl.await().toString()
    }
    fun  logout(){
        userRepository.logout()

    }
}