package com.nhathuy.gas24h_7app.fragment.delete

interface DeleteAccountContract {
    interface View{
        fun showMessage(message:String)
        fun navigateMain()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun deleteAccount()
    }
}