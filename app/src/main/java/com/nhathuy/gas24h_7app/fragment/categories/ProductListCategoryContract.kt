package com.nhathuy.gas24h_7app.fragment.categories

import com.nhathuy.gas24h_7app.data.model.Product

interface ProductListCategoryContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showProducts(products:List<Product>)
        fun showError(message:String)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun fetchProduct(categoryId:String)
    }
}