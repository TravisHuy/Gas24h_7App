package com.nhathuy.gas24h_7app.fragment.profile

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.User

interface ProfileContract {
    interface View{
        fun navigatePurchaseOrder()
        fun showUpdateProfileImage(imageUrl:String)
        fun showError(message: String)
        fun showLoading(isLoading: Boolean)
        fun showMessage(message: String)
        fun showUserInfo(user:User)
        fun showBtnLogin()
        fun hideBtnLogin()
        fun showDialogLogout()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun updateProfileImage(imageUri: Uri)

        fun loadUserInfo()
    }
}