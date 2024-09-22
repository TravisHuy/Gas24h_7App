package com.nhathuy.gas24h_7app.fragment.profile

import android.net.Uri

interface ProfileContract {
    interface View{
        fun navigatePurchaseOrder()
        fun showUpdateProfileImage(imageUrl:String)
        fun showError(message: String)
        fun showLoading(isLoading: Boolean)
        fun showMessage(message: String)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun updateProfileImage(imageUri: Uri)
    }
}