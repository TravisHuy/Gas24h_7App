package com.nhathuy.gas24h_7app.fragment.hotline

interface HotlineContract {

    interface View{
        fun navigateHome()
        fun makePhoneCall(phoneNumber: String)
    }
    interface Presenter{
        fun attachView(view: View)
        fun detachView()
        fun onCallButtonClicked()
        fun onCancelButtonClicked()
    }
}