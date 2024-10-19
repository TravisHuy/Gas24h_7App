package com.nhathuy.gas24h_7app.ui.search

import com.nhathuy.gas24h_7app.data.model.Product

interface SearchContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
        fun showSearchResults(products:List<Product>)
//        fun showRecentSearches(searches: List<Product>)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun searchProducts(query:String)
//        fun getRecentSearches()
    }
}