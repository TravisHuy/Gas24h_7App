package com.nhathuy.gas24h_7app.admin

interface AdminContract {
    interface View{
        fun showMessage(message:String)
        fun navigateToMain()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun getAdmin()
        fun checkAdminPermission()
    }
}