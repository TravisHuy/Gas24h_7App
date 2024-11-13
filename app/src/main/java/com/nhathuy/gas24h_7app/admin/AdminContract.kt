package com.nhathuy.gas24h_7app.admin

interface AdminContract {
    interface View{
        fun showMessage(message:String)
        fun showCountOrderShipping(count:Int)
        fun showCountOrderCancel(count:Int)
        fun navigateToMain()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadOrderShipping()
        fun loadOrderCancel()
        fun getAdmin()
        fun checkAdminPermission()
    }
}