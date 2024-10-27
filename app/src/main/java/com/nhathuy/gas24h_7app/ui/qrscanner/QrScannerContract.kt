package com.nhathuy.gas24h_7app.ui.qrscanner

interface QrScannerContract {
    interface View{
        fun navigateToProductDetail(productId: String)
        fun showError(message: String)
    }
    interface Presenter{
        fun attachView(view: View)
        fun detachView()
    }
}