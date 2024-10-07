package com.nhathuy.gas24h_7app.ui.detail_product

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.repository.CartRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.ReviewRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailProductPresenter @Inject constructor(private val productRepository: ProductRepository,
                                                private val cartRepository: CartRepository,
                                                private val userRepository: UserRepository,
                                                private val reviewRepository: ReviewRepository
):DetailProductContract.Presenter{

    private var view:DetailProductContract.View?=null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)
    private var currentProduct:Product?=null
    override fun attachView(view: DetailProductContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
        job.cancel()
    }

    override fun loadProductDetails(productId: String) {
        view?.showLoading()
        coroutineScope.launch {
            val result = productRepository.getProductById(productId)
            view?.hideLoading()
            result.fold(
                onSuccess = {
                    product ->
                    currentProduct=product
                    view?.showProductDetails(product)
                    view?.setupImageSlider(product.detailImageUrls)
                },
                onFailure = {e->
                    view?.showError("${e.message}")
                }
            )
        }
    }

    override fun loadSuggestProducts(currentProductId: String) {
        coroutineScope.launch {
            val result = productRepository.getProductsByCategoryLimitCount(currentProductId,6)
            result.fold(
                onSuccess = {
                    products ->
                            view?.setupSuggestProduct(products)
                },
                onFailure = {e->
                    view?.showError("${e.message}")
                }
            )
        }
    }

    override fun addToCart(productId: String, quantity: Int, price: Double) {
        coroutineScope.launch {
            val userId= userRepository.getCurrentUserId()
            if(userId!=null){
                val result=cartRepository.addToCart(userId, productId, quantity, price)
                result.fold(
                    onSuccess = {view?.showSuccess("Added to cart")
                                updateCartCount(userId)
                                },
                    onFailure = {e->view?.showError(e.message?:"Unknown error occured")}
                )
            }
            else{
                view?.showError("Failed add to cart")
            }
        }
    }

    private suspend fun updateCartCount(userId: String) {
        coroutineScope.launch {
            val currentCountResult = cartRepository.getCartItemCount(userId)
            currentCountResult.fold(
                onSuccess = { count ->
                    view?.updateCartItemCount(count)
                },
                onFailure = { e -> view?.showError(e.message ?: "Failed to update cart count") }
            )
        }
    }

    override fun loadCartItemCount() {
        coroutineScope.launch {
            val userId= userRepository.getCurrentUserId()
            if(userId!=null){
                val result = cartRepository.getCartItemCount(userId)
                result.fold(
                    onSuccess = {count -> view?.updateCartItemCount(count)},

                    onFailure = {e->view?.showError(e.message?:"Failed to load cart item count")}
                )
            }
        }
    }

    override fun loadProductSoldCount(productId: String) {
        coroutineScope.launch {
            val result = productRepository.getProductSoldCount(productId)
            result.fold(
                onSuccess = {
                   count -> view?.showProductSoldCount(count)
                },
                onFailure = {
                        e->view?.showError(e.message?:"Failed to load product sold count")
                }
            )
        }
    }

    override fun onDecreaseQuantity(currentQuantity: Int,stockCount: Int) {
        if (currentQuantity > 1) {
            val newQuantity = currentQuantity - 1
            view?.updateQuantity(newQuantity)
            view?.setAddToCartButtonEnabled(true)
        }
    }

    override fun onIncreaseQuantity(currentQuantity: Int,stockCount: Int) {
        val newQuantity = currentQuantity + 1
        if (newQuantity <= stockCount) {
            view?.updateQuantity(newQuantity)
            view?.setAddToCartButtonEnabled(true)
        }
    }

    override fun onQuantityChanged(quantity: Int, stockCount: Int) {
        val isEnabled= quantity in 1..stockCount
        view?.setAddToCartButtonEnabled(isEnabled)
    }

    override fun onAddToCartClicked(productId: String, quantity: Int, price: Double) {
        coroutineScope.launch {
            val userId=userRepository.getCurrentUserId()
            if(userId!=null){
                val currentQuantityResult =cartRepository.getCartItemQuantity(userId,productId)
                currentQuantityResult.fold(
                    onSuccess = {currentQuantity ->
                        val totalQuantity = currentQuantity + quantity
                        currentProduct?.let {
                            product ->
                            if(totalQuantity<=product.stockCount){
                                addToCartImpl(userId,productId, quantity, price)
                            }
                            else{
                                view?.showQuantityExceededDialog(currentQuantity)
                            }
                        }
                    },
                    onFailure = {e ->
                        view?.showError(e.message ?: "Failed to check current quantity")
                    }
                )
            }
            else{
                view?.showError("Failed to add to cart")
            }
        }
    }

    private fun addToCartImpl(userId: String, productId: String, quantity: Int, price: Double){
        coroutineScope.launch {
            val result = cartRepository.addToCart(userId, productId, quantity, price)
            result.fold(
                onSuccess = {
                    view?.showSuccess("Added to cart")
                    updateCartCount(userId)
                    view?.dismissAddToCartDialog()
                },
                onFailure = { e ->
                    view?.showError(e.message ?: "Unknown error occurred")
                }
            )
        }
    }

    override fun loadReviews(productId: String) {
        coroutineScope.launch {
            try {
                val result = reviewRepository.getAllReviewsWithProductId(productId)
                result.fold(
                    onSuccess = { reviews->
                        val userId = reviews.map {
                            it.userId
                        }
                        val userMap = mutableMapOf<String,User>()
                        val userJobs = userId.map {
                                userId ->
                            async(Dispatchers.IO) {
                                val userResult = userRepository.getUser(userId)
                                userResult.getOrNull()?.let {
                                        user -> userMap[userId] = user
                                }
                            }
                        }
                        userJobs.forEach {
                            it.await()
                        }
                        view?.showReviews(reviews,userMap)
                    },
                    onFailure = { e->
                        view?.showError("Failed load review ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showError("Failed load review ${e.message}")
            }
        }
    }
}