package com.nhathuy.gas24h_7app.ui.order

import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.ui.cart.CartContract

interface OrderContract {
    interface View{
        fun showOrderItems(items: MutableList<CartItem>, products: MutableMap<String, Product>)
        fun showError(message: String)
    }
    interface Presenter{
        fun attachView(view: OrderContract.View)
        fun detachView()
        fun loadOrderItems(selectedItems: MutableList<CartItem>)
    }
}
