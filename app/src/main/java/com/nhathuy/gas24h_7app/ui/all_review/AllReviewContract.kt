package com.nhathuy.gas24h_7app.ui.all_review

import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.User

interface AllReviewContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
        fun showReviews(reviews: List<Review>, users:Map<String, User>)
        fun showReviewsHaveVideoOrImage(reviews: List<Review>, users:Map<String, User>)
        fun showFilteredReviews(reviews: List<Review>, users: Map<String, User>)
        fun showDialogStar()
        fun updateCartItemCount(count: Int)
        fun updateReviewsCounts(totalCount: Int, imageVideoCount:Int)
        fun updateStartCount(starCounts:  Map<Int,Int>)
        fun navigateCart()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadReviews(productId:String)
        fun loadCartItemCount()
        fun loadReviewsHaveVideoOrImage(productId: String)
        fun loadReviewsByRating(productId:String,rating:Float)
    }
}