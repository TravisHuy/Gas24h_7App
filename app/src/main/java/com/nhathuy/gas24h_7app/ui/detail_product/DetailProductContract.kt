package com.nhathuy.gas24h_7app.ui.detail_product

import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.User

interface DetailProductContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showSuccess(message: String)
        fun showError(message:String)
        fun showProductDetails(product: Product)
        fun setupImageSlider(detailImages:List<String>)
        fun setupSuggestProduct(products:List<Product>)
        fun backHome()
        fun setupBottomNavigation()
        fun updateCartItemCount(count: Int)
        fun setupCartBadge()
        fun navigateCart()
        fun navigateHotline()
        fun navigateAllReviews()
        fun showAddToCartDialog(product: Product)
        fun showReviews(reviews: List<Review>,users:Map<String,User>)
        fun updateQuantity(quantity: Int)
        fun dismissAddToCartDialog()
        fun setAddToCartButtonEnabled(isEnabled:Boolean)
        fun showQuantityExceededDialog(currentQuantity: Int)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadProductDetails(productId:String)
        fun loadSuggestProducts(categoryId:String)
        fun addToCart(productId: String,quantity:Int,price:Double)
        fun loadCartItemCount()
        fun onDecreaseQuantity(currentQuantity:Int,stockCount: Int)
        fun onIncreaseQuantity(currentQuantity: Int,stockCount: Int)
        fun onQuantityChanged(quantity: Int,stockCount:Int)
        fun onAddToCartClicked(productId: String,quantity: Int,price: Double)

        //load review
        fun loadReviews(productId: String)
    }
}