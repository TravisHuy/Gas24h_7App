
package com.nhathuy.gas24h_7app.ui.add_review

import android.net.Uri
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

    private var productId:String?=null

    private val images = mutableListOf<Uri>()
    private var video: Uri? = null

    private val MAX_IMAGES = 3
    private val MAX_VIDEOS = 1
    override fun attachView(view: AddReviewContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    //load information order
    override fun loadOrder(orderId: String) {
        coroutineScope.launch {
            try {
                val result = orderRepository.getOrderId(orderId)
                result.fold(
                    onSuccess = {order->
                        productId = order.items.firstOrNull()?.productId
                        productId?.let { loadProduct(it) }
                    },
                    onFailure = {e->
                        view?.showMessage("Failed to load order: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showMessage("Failed to load order: ${e.message}")
            }
        }
    }
    //load information product
    private fun loadProduct(productId:String) {
        coroutineScope.launch {
            try {
                val result = productRepository.getProductById(productId)
                result.fold(
                    onSuccess = { product->
                        view?.showInformationProduct(product)
                    },
                    onFailure = { e->
                        view?.showMessage("Failed load product ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showMessage("Failed load product ${e.message}")
            }
        }
    }
    override fun onImageAdded(uri: Uri) {
        if(images.size<MAX_IMAGES){
            images.add(uri)
            view?.addImageToAdapter(uri.toString())
            view?.updateImageCount(images.size, Constants.MAX_IMAGE_COUNT)
            view?.enableImageAddButton(images.size < Constants.MAX_IMAGE_COUNT)
        }
        else{
            view?.showMessage("Maximum number of images reached")
        }
    }

    override fun onImageRemoved(position: Int) {
        if (position in 0 until images.size) {
            images.removeAt(position)
            view?.removeImageFromAdapter(position)
            view?.updateImageCount(images.size, MAX_IMAGES)
            view?.enableImageAddButton(true)
        }
    }

    override fun onVideoAdded(uri: Uri) {
        if(video==null){
            video=uri
            view?.onVideoAdded(uri)
            view?.enableCoverImageAddButton(false)
            view?.updateVideoCount(1,MAX_VIDEOS)
        }
    }

    override fun onVideoRemoved() {
        video=null
        view?.clearVideo()
        view?.enableCoverImageAddButton(true)
        view?.updateVideoCount(0,MAX_VIDEOS)
    }

    override fun submitReview(rating: Float, comment: String) {
        view?.showLoading()
        coroutineScope.launch {
            try {
                val userId=userRepository.getCurrentUserId()

                val review= Review(
                    id = UUID.randomUUID().toString(),
                    productId = productId!!,
                    userId = userId!!,
                    rating = rating,
                    comment = comment,
                    timestamp = System.currentTimeMillis(),
                    images = emptyList(),
                    video = "",
                    reviewStatus = ReviewStatus.fromStars(rating.toInt())
                )

                val result = reviewRepository.createReview(review,images,video)
                result.fold(
                    onSuccess = {
                        view?.showMessage("Review submitted successfully")
                        view?.clearInputField()
                        view?.clearImages()
                        view?.clearVideo()
                        images.clear()
                        video = null
                        view?.navigateBack()
                    },
                    onFailure = { e ->
                        view?.showMessage("Failed to submit review: ${e.message}")
                    }
                )
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
