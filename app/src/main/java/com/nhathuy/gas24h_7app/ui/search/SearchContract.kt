package com.nhathuy.gas24h_7app.ui.search

interface SearchContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
    }
}