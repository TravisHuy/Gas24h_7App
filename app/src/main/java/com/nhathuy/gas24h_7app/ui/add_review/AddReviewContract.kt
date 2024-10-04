package com.nhathuy.gas24h_7app.ui.add_review

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review

interface AddReviewContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showMessage(message: String)
        fun showProducts(products: List<Product>)
        fun onReviewSubmitted()
        fun updateImageCount(position: Int, count: Int)
        fun updateVideoState(position: Int, hasVideo: Boolean)
        fun updateRatingDisplay(position: Int, rating: Float)
        fun enableSubmitButton(enable: Boolean)
        fun navigateBack()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadProductsFromOrder(orderId: String)
        fun submitReviews(reviews:List<Review>)
        fun handleImageAdded(position: Int)
        fun handleVideoAdded(position: Int)
        fun handleRatingChanged(position: Int, rating: Float)
        fun validateReviews(reviews: List<Review>)
    }
}