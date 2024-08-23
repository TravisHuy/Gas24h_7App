package com.nhathuy.gas24h_7app.admin.voucher.all_product

import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductContract
import java.util.Calendar

interface VoucherAllContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun showSuccess(message: String)
        fun clearFields()
        fun updateDateTimeDisplay(calendar: Calendar, isStartDate: Boolean)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun createVoucher(voucher: Voucher)
        fun validateVoucherInput(voucher: Voucher):Boolean
        fun onStartDateSelected(year: Int, month: Int, day: Int)
        fun onStartTimeSelected(hourOfDay: Int, minute: Int)
        fun onEndDateSelected(year: Int, month: Int, day: Int)
        fun onEndTimeSelected(hourOfDay: Int, minute: Int)
        fun getStartDate(): Calendar
        fun getEndDate(): Calendar
        fun resetDates()
    }

}