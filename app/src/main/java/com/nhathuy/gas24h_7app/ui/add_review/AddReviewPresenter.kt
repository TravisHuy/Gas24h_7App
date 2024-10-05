package com.nhathuy.gas24h_7app.ui.add_review

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.ReviewStatus
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.ReviewRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class AddReviewPresenter @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository
) : AddReviewContract.Presenter {

    private var view: AddReviewContract.View? = null

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private var orderId: String? = null
    private val reviews = mutableListOf<Review>()
    private val products = mutableMapOf<String, Product>()

    override fun attachView(view: AddReviewContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadOrder(orderId: String) {
        view?.showLoading()
        coroutineScope.launch {
            try {
                val result = orderRepository.getOrderId(orderId)
                this@AddReviewPresenter.orderId = orderId

                result.fold(
                    onSuccess = { order ->
                        reviews.clear()
                        products.clear()
                        order.items.forEach { item ->
                            val productResult = productRepository.getProductById(item.productId)
                            productResult.fold(
                                onSuccess = { product ->
                                    products[product.id] = product
                                    reviews.add(
                                        Review(
                                            id = UUID.randomUUID().toString(),
                                            productId = product.id,
                                            userId = userRepository.getCurrentUserId() ?: "",
                                            rating = 5f,
                                            comment = "",
                                            date = Date(),
                                            images = emptyList(),
                                            video = "",
                                            reviewStatus = ReviewStatus.FIVE_STARS
                                        )
                                    )
                                },
                                onFailure = { e ->
                                    view?.showMessage("Failed to load product: ${e.message}")
                                }
                            )
                        }
                        view?.updateAdapter(reviews, products)
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

    override fun onImageAdded(reviewIndex: Int, uri: Uri) {
        if (reviews[reviewIndex].images.size < Constants.MAX_IMAGES) {
            reviews[reviewIndex] = reviews[reviewIndex].copy(
                images = reviews[reviewIndex].images + uri.toString()
            )
            view?.updateAdapter(reviews, products)
        } else {
            view?.showMessage("Maximum number of images reached")
        }
    }

    override fun onImageRemoved(reviewIndex: Int, position: Int) {
        reviews[reviewIndex] = reviews[reviewIndex].copy(
            images = reviews[reviewIndex].images.filterIndexed { index, _ -> index != position }
        )
        view?.updateAdapter(reviews, products)
    }

    override fun onVideoAdded(reviewIndex: Int, uri: Uri) {
        reviews[reviewIndex] = reviews[reviewIndex].copy(video = uri.toString())
        view?.updateAdapter(reviews, products)
    }

    override fun onVideoRemoved(reviewIndex: Int) {
        reviews[reviewIndex] = reviews[reviewIndex].copy(video = "")
        view?.updateAdapter(reviews, products)
    }

    override fun submitReviews() {
        view?.showLoading()
        coroutineScope.launch {
            try {
                reviews.forEach { review ->
                    reviewRepository.createReview(review)
                }
                orderRepository.updateOrderStatus(orderId!!, OrderStatus.RATED)
                view?.showMessage("Reviews submitted successfully")
                view?.navigateBack()
            } catch (e: Exception) {
                view?.showMessage("Failed to submit reviews: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    override fun updateReview(reviewIndex: Int, rating: Float, comment: String) {
        reviews[reviewIndex] = reviews[reviewIndex].copy(
            rating = rating,
            comment = comment,
            reviewStatus = ReviewStatus.fromStars(rating.toInt())
        )
        view?.updateAdapter(reviews, products)
    }
}