package com.nhathuy.gas24h_7app.ui.detail_product

import com.nhathuy.gas24h_7app.data.model.Product

interface DetailProductContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun showProductDetails(product: Product)
        fun setupImageSlider(detailImages:List<String>)
        fun setupSuggestProduct(products:List<Product>)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadProductDetails(productId:String)
        fun loadSuggestProducts(categoryId:String)
    }
}