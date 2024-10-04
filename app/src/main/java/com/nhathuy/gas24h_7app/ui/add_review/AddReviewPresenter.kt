
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
class AddReviewPresenter @Inject constructor(private val reviewRepository: ReviewRepository,
                                             private val userRepository: UserRepository,
                                             private val productRepository: ProductRepository,
                                             private val orderRepository: OrderRepository
):AddReviewContract.Presenter {

    private var view:AddReviewContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    override fun attachView(view: AddReviewContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadProductsFromOrder(orderId: String) {
        coroutineScope.launch {
            try {
                view?.showLoading()
                val order = orderRepository.getOrderId(orderId).getOrThrow()
                val productIds = order.items.map { it.productId }
                val products = productRepository.getProductByIds(productIds).getOrThrow()
                view?.showProducts(products)
            }
            catch (e:Exception){
                view?.showMessage("Failed to load product: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun submitReviews(reviews: List<Review>) {
        coroutineScope.launch {
            try {
                view?.showLoading()
                reviews.forEach { review ->
                    reviewRepository.createReview(review).getOrThrow()
                }
                view?.onReviewSubmitted()
            }
            catch (e:Exception){
                view?.showMessage("Failed to submit reviews: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun handleImageAdded(position: Int) {
        TODO("Not yet implemented")
    }

    override fun handleVideoAdded(position: Int) {
        TODO("Not yet implemented")
    }

    override fun handleRatingChanged(position: Int, rating: Float) {
        TODO("Not yet implemented")
    }

    override fun validateReviews(reviews: List<Review>) {
        TODO("Not yet implemented")
    }


}