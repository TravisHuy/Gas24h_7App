package com.nhathuy.gas24h_7app.ui.qrscanner

import javax.inject.Inject

class QrScannerPresenter @Inject constructor() : QrScannerContract.Presenter {
    private var view: QrScannerContract.View? = null

    override fun attachView(view: QrScannerContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }
}