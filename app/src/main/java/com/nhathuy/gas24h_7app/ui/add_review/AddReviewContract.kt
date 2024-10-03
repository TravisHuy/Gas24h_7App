package com.nhathuy.gas24h_7app.ui.add_review

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review

interface AddReviewContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showMessage(message: String)
        fun updateProductList(products: List<Product>)
        fun updateReviewUI(position: Int, review: Review)
        fun updateImageAddButton(position: Int, enabled: Boolean)
        fun updateVideoAddButton(position: Int, enabled: Boolean)
        fun navigateBack()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadOrder(orderId: String)
        fun onImageAdded(position: Int, uri: Uri)
        fun onImageRemoved(position: Int, imagePosition: Int)
        fun onVideoAdded(position: Int, uri: Uri)
        fun onVideoRemoved(position: Int)
        fun updateReview(position: Int, rating: Float, comment: String)
        fun submitReviews()
    }
}