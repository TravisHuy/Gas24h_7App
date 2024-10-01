package com.nhathuy.gas24h_7app.ui.all_review

import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.User

interface AllReviewContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
        fun showReviews(reviews: List<Review>, users:Map<String, User>)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadReviews(productId:String)
    }
}