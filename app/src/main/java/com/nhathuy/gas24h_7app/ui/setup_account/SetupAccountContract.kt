package com.nhathuy.gas24h_7app.ui.setup_account

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.User

interface SetupAccountContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
        fun showError(message: String)
        fun showUpdateProfileImage(imageUrl:String)
        fun showInfoUser(user:User)
        fun setAddressFields(province:String,district:String,ward:String,houseNumber:String)
        fun getUserFullName():String
        fun getProvince():String
        fun getDistrict():String
        fun getWard():String
        fun getHouseName():String
        fun onSubmitted()
        fun navigateGoogleMap()
        fun navigateProfile()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun updateProfileImage(imageUri: Uri)
        fun loadUserInfo()
        fun parseAndSetAddress(address:String)
        fun getCurrentAddress():String?
        fun onSubmit(user:User)
    }
}