package com.nhathuy.gas24h_7app.ui.login

import com.nhathuy.gas24h_7app.adapter.Country

interface LoginContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun showCountries(countries:List<Country>)
        fun navigateVerification(verificationId:String)
    }
    interface Presenter{
        fun attachView(view: LoginContract.View)
        fun detachView()
        fun loadCountries()
        fun onCountrySelected(country: Country)
        fun sendVerification(phoneNumber:String)
    }
}