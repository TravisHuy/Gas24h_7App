package com.nhathuy.gas24h_7app.admin.voucher.detail_product

import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Voucher
import java.util.Calendar


interface VoucherDetailContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun showSuccess(message: String)
        fun clearFields()
        fun updateDateTimeDisplay(calendar: Calendar, isStartDate: Boolean)
        fun updateProductList(products: List<Product>, selectedProducts: MutableSet<String>)
        fun updateSelectAllCheckbox(isAllSelected:Boolean)
    }
    interface Presenter{
        fun attachView(view: View)
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
        fun loadProducts()
        fun searchProducts(query: String)
        fun getSelectedProducts(): List<Product>
        fun  updateItemSelection(productId:String,isChecked:Boolean)
        fun toggleSelectAll(isChecked: Boolean)
    }
}