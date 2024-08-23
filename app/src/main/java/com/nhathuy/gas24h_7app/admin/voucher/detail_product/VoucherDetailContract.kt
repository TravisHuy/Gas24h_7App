package com.nhathuy.gas24h_7app.admin.voucher.detail_product


interface VoucherDetailContract {
    interface View{
        fun showError(message:String)
    }
    interface Presenter{
        fun attachView(view: View)
        fun detachView()
    }
}