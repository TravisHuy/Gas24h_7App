package com.nhathuy.gas24h_7app.ui.order_information

import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User

interface OrderInformationContract {
    interface View{
        fun showError(message:String)
        fun showMessage(message: String)
        fun showUserInfo(user: User)
        fun showOrder(order:Order, products:Map<String, Product>)
        fun setupSuggestProduct(products: List<Product>)
        fun navigateCall()
        fun navigatePurchase()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadUserInfo()
        fun loadOrder(orderId:String)
        fun loadSuggestProducts()
        fun cancelOrder(orderId: String)
    }
}