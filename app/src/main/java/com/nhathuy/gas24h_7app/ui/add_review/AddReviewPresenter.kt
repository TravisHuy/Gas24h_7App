
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

    private var orderId: String? = null
    private val reviews = mutableListOf<Review>()
    private val products = mutableListOf<Product>()

    override fun attachView(view: AddReviewContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    //load information order
    override fun loadOrder(orderId: String) {
        this.orderId= orderId
        coroutineScope.launch {
            try {
                val order = orderRepository.getOrderId(orderId).getOrNull()
                val productIds = order!!.items.map { it.productId }
                products.clear()
                reviews.clear()
                productIds.forEach {
                        productId ->
                    val product = productRepository.getProductById(productId).getOrNull()
                    products.add(product!!)
                    reviews.add(createInitialReview(product.id))
                }
                view?.updateProductList(products)
                reviews.forEachIndexed { index, _ ->
                    view?.updateImageAddButton(index,true)
                    view?.updateVideoAddButton(index, true)
                }
            }
            catch (e:Exception){
                view?.showMessage("Failed to load order: ${e.message}")
            }
        }
    }

    private fun createInitialReview(productId: String): Review {
        return Review(
            id = UUID.randomUUID().toString(),
            productId = productId,
            userId = userRepository.getCurrentUserId()?:"",
            rating = 0f,
            comment = "",
            date = Date(),
            images = emptyList(),
            video = "",
            reviewStatus = ReviewStatus.FIVE_STARS
        )
    }

    override fun onImageAdded(position:Int,uri: Uri) {
        if(reviews[position].images.size<Constants.MAX_IMAGES){
            val updateImages = reviews[position].images.toMutableList().apply { add(uri.toString()) }
            reviews[position] =reviews[position].copy(images = updateImages)

            view?.updateReviewUI(position, reviews[position])
            view?.updateImageAddButton(position,updateImages.size<Constants.MAX_IMAGES)
        }
        else{
            view?.showMessage("Maximum number of images reached for this product")
        }
    }

    override fun onImageRemoved(position: Int,imagePosition:Int) {
        val updatedImages = reviews[position].images.toMutableList().apply { removeAt(imagePosition) }
        reviews[position] = reviews[position].copy(images = updatedImages)
        view?.updateReviewUI(position, reviews[position])
        view?.updateImageAddButton(position, true)
    }

    override fun onVideoAdded(position: Int,uri: Uri) {
        reviews[position] = reviews[position].copy(video = uri.toString())
        view?.updateReviewUI(position, reviews[position])
        view?.updateVideoAddButton(position, false)
    }

    override fun onVideoRemoved(position: Int) {
        reviews[position] = reviews[position].copy(video = "")
        view?.updateReviewUI(position, reviews[position])
        view?.updateVideoAddButton(position, true)
    }
    override fun updateReview(position: Int, rating: Float, comment: String) {
        reviews[position] = reviews[position].copy(
            rating = rating,
            comment = comment,
            reviewStatus = ReviewStatus.fromStars(rating.toInt())
        )
        view?.updateReviewUI(position, reviews[position])
    }
    override fun submitReviews() {
        view?.showLoading()
        coroutineScope.launch {
            try {
                reviews.forEach {
                        review ->
                    reviewRepository.createReview(review).getOrThrow()
                }
                orderRepository.updateOrderStatus(orderId!!,OrderStatus.RATED)
                view?.showMessage("Reviews submitted successfully")
                view?.navigateBack()
            }
            catch (e:Exception){
                view?.showMessage("Failed to submit review: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }


}