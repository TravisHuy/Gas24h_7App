package com.nhathuy.gas24h_7app.ui.add_review

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review

interface AddReviewContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showMessage(message: String)
        fun updateAdapter(reviews: List<Review>, products: Map<String, Product>)
        fun navigateBack()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadOrder(orderId: String)
        fun onImageAdded(reviewIndex: Int, uri: Uri)
        fun onImageRemoved(reviewIndex: Int, position: Int)
        fun onVideoAdded(reviewIndex: Int, uri: Uri)
        fun onVideoRemoved(reviewIndex: Int)
        fun submitReviews()
        fun updateReview(reviewIndex: Int, rating: Float, comment: String)
    }
}