package com.nhathuy.gas24h_7app.ui.purchased_order

import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.Product

interface PurchasedOrderContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun showOrders(orders:List<Order>,products:Map<String, Product>)
        fun setupSuggestProduct(products: List<Product>)
        fun showOrderStatusUpdateSuccess()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadOrders(status:String)
        fun loadSuggestProducts()
        fun updateOrderStatus(orderId: String, newStatus: OrderStatus)
    }
}