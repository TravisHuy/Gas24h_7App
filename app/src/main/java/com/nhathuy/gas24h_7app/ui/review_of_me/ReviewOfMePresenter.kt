package com.nhathuy.gas24h_7app.ui.review_of_me

import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.ReviewRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReviewOfMePresenter @Inject constructor(private val userRepository: UserRepository,
                                              private val reviewRepository: ReviewRepository,
                                              private val productRepository: ProductRepository,
                                              private val orderRepository: OrderRepository
): ReviewOfMeContract.Presenter {

    private var view:ReviewOfMeContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)


    override fun attachView(view: ReviewOfMeContract.View) {
        this.view = view
    }

    override fun detachView() {
       view = null
       job.cancel()
    }

    override fun loadOrders(status: String) {
        coroutineScope.launch {
            try {
                val userId = userRepository.getCurrentUserId()

                val result = orderRepository.getOrdersForUser(userId!!, status)
                view?.showLoading()
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
                        view?.showMessage("Failed to load order: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                view?.showMessage("Failed to load order: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    override fun loadReviews() {
        coroutineScope.launch {
            try {
                view?.showLoading()
                val result = reviewRepository.getAllReviewsUserId(userRepository.getCurrentUserId()!!)
                result.fold(
                    onSuccess = { reviews ->
                        val userId = reviews.map {
                            it.userId
                        }
                        val userMap = mutableMapOf<String,User>()
                        val userJobs = userId.map { userId->
                            async(Dispatchers.IO) {
                                val userResult = userRepository.getUser(userId)
                                userResult.getOrNull()?.let {
                                    user -> userMap[userId] = user
                                }
                            }
                        }
                        userJobs.forEach { it.await() }
                        view?.showReviews(reviews,userMap)
                    },
                    onFailure = { e->
                        view?.showMessage("Failed to load order: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showMessage("Failed to load order: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

}