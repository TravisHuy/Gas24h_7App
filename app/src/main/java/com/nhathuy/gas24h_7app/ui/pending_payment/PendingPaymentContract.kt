package com.nhathuy.gas24h_7app.ui.pending_payment

import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductContract

interface PendingPaymentContract {

    interface View{
        fun showError(message:String)
        fun setupSuggestProduct(products:List<Product>)
        fun updateCartItemCount(count:Int)
        fun backHome()
        fun setupCartBadge()
        fun navigateCart()
    }
    interface Presenter{
        fun attachView(view: View)
        fun detachView()
        fun loadSuggestProducts()
        fun loadCartItemCount()
    }
}