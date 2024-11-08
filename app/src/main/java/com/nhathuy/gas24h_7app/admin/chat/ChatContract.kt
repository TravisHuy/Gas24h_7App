package com.nhathuy.gas24h_7app.admin.chat

interface ChatContract {
    interface View{
        fun showLoading()
        fun hideLoading()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
    }
}