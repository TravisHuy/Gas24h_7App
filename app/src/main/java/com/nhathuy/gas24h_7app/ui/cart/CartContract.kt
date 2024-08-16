package com.nhathuy.gas24h_7app.ui.cart

import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductContract

interface CartContract {
    interface View{
        fun showCartItems(cartItems: List<CartItem>, products: Map<String, Product>)
        fun showError(message: String)
        fun updateCartItemQuantity(productId: String, newQuantity: Int)
    }
    interface Presenter{
        fun attachView(view: CartContract.View)
        fun detachView()
        fun loadCartItems()
        fun updateCartItemQuantity(productId: String, newQuantity: Int)
    }
}