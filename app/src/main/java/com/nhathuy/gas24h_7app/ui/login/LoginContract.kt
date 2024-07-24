package com.nhathuy.gas24h_7app.ui.login

import com.nhathuy.gas24h_7app.adapter.Country

interface LoginContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun navigateVerification(verificationId:String)
    }
    interface Presenter{
        fun setView(view: View)
        fun onCountrySelected(country: Country)
        fun sendVerification(phoneNumber:String)
    }
}