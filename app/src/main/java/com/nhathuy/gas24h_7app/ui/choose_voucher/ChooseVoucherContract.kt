package com.nhathuy.gas24h_7app.ui.choose_voucher

import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Voucher


interface ChooseVoucherContract {
    interface View{
        fun showError(message:String)
        fun updateVoucherList(products: List<Voucher>, selectedVoucher: String?)
        fun updateTvDiscountPrice(discountInfo:String?)
        fun finishWithResult(selectedVoucher: Voucher?)
    }
    interface Presenter{
        fun attachView(view: View)
        fun detachView()
        fun loadVouchers()
        fun searchVouchers(query:String)
        fun  updateItemSelection(voucherId:String,isChecked:Boolean)
        fun searchAndSelectFirstVoucher(query: String)
        fun setHasSelectedProducts(hasSelected: Boolean)
        fun onAgreeButtonClick()
    }
}