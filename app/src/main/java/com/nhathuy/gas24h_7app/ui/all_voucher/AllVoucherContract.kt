package com.nhathuy.gas24h_7app.ui.all_voucher

import com.nhathuy.gas24h_7app.data.model.Voucher

interface AllVoucherContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun showVoucher(vouchers:List<Voucher>)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadVouchers()
    }
}