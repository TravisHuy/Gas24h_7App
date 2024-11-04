package com.nhathuy.gas24h_7app.fragment.chat

import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product

interface ChatContract {

    interface View{
        fun showLoading()
        fun hideLoading()
        fun showOrders(orders:List<Order>,products:Map<String, Product>)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadOrders()
        fun getCurrentUserId():String?
    }

}