package com.nhathuy.gas24h_7app.fragment.home

import com.nhathuy.gas24h_7app.data.model.ProductCategory

interface HomeFragmentContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun showBanners(banners:List<String>)

        fun showCategories(categories:List<ProductCategory>)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun fetchCategories()
        fun fetchBanners()
    }
}