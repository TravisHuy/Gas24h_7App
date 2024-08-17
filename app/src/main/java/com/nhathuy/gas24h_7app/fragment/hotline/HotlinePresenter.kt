package com.nhathuy.gas24h_7app.fragment.hotline

import com.nhathuy.gas24h_7app.util.Constants.PHONE_HOTLINE

class HotlinePresenter():HotlineContract.Presenter {

    private var view:HotlineContract.View? = null

    override fun attachView(view: HotlineContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
    }

    override fun onCallButtonClicked() {
        view?.makePhoneCall(PHONE_HOTLINE)
    }

    override fun onCancelButtonClicked() {
        view?.navigateHome()
    }

}