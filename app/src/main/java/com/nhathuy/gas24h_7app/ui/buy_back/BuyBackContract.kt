package com.nhathuy.gas24h_7app.ui.buy_back

import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.Product

interface BuyBackContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun showOrders(orders:List<Order>, products:Map<String, Product>)
        fun onUpdateOrderStatus(orderId: String, newStatus: String)
        fun updateCartItemCount(count: Int)
        fun navigateCart()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadOrders(status:String)
        fun updateOrderStatus(orderId: String, newStatus: OrderStatus)
        fun loadCartItemCount()
    }
}