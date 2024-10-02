package com.nhathuy.gas24h_7app.ui.review_of_me

import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.User

interface ReviewOfMeContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
        fun showOrders(orders:List<Order>, products:Map<String, Product>)
        fun showReviews(reviews: List<Review>, users:Map<String, User>)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadOrders(status:String)
        fun loadReviews()
    }
}