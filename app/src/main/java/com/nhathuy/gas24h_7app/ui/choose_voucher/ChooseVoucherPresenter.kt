package com.nhathuy.gas24h_7app.ui.choose_voucher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.DiscountType
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.data.repository.VoucherRepository
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChooseVoucherPresenter @Inject constructor(private val voucherRepository: VoucherRepository,
                                                 private val context:Context):ChooseVoucherContract.Presenter {

    private var view:ChooseVoucherContract.View?=null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)
    private var allVouchers: List<Voucher> = listOf()
    private var selectedVoucherId:String?=null
    private var currentVoucherId: String? = null
    private var hasSelectedProducts: Boolean = false
    override fun attachView(view: ChooseVoucherContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
        job.cancel()
    }

    override fun loadVouchers() {
        coroutineScope.launch {
            val result = voucherRepository.getAllVouchers()
            result.fold(
                onSuccess = {
                    vouchers ->
                    allVouchers=vouchers
                    view?.updateVoucherList(vouchers,selectedVoucherId)
                    currentVoucherId?.let { updateItemSelection(it,true) }
                },
                onFailure = {e ->
                    view?.showError("Failed to voucher: ${e.message}")
                    Log.d("CHooseActivity","${e.message}")
                }
            )
        }
    }

    override fun setCurrentVoucherId(voucherId: String?) {
        currentVoucherId=voucherId
        selectedVoucherId=voucherId
    }

    override fun searchVouchers(query:String) {
        val filteredVouchers= allVouchers.filter {
            voucher ->
            voucher.id.contains(query,ignoreCase = true) || voucher.code?.contains(query,ignoreCase = true)==true
        }
        view?.updateVoucherList(filteredVouchers, selectedVoucherId)
    }

    override fun updateItemSelection(voucherId: String, isChecked: Boolean) {

        if(isChecked && voucherId != selectedVoucherId){
            selectedVoucherId = voucherId
        }
        else{
            selectedVoucherId =null
        }
        view?.updateVoucherList(allVouchers, selectedVoucherId)

        if(selectedVoucherId != null){
            val selectVoucher = allVouchers.find { it.id == voucherId }
            selectVoucher?.let {
                updateDiscountInfo(it)
            }
        }
        else{
            view?.updateTvDiscountPrice(null)
        }
    }

    private fun updateDiscountInfo(voucher: Voucher) {
        when (voucher.discountType) {
            DiscountType.FIXED_AMOUNT -> {
                val formattedDiscount = NumberFormatUtils.formatDiscount(voucher.discountValue)
                val discountInfo =
                    context.getString(R.string.tv_voucher_discount, formattedDiscount)
                view?.updateTvDiscountPrice(discountInfo)
            }

            DiscountType.PERCENTAGE -> {
                val discountInfo = context.getString(
                    R.string.tv_voucher_discount_percent,
                    voucher.discountValue
                )
                view?.updateTvDiscountPrice(discountInfo)
            }
        }
    }

    override fun searchAndSelectFirstVoucher(query: String) {
        val filteredVouchers = allVouchers.filter { voucher ->
            voucher.id.contains(query, ignoreCase = true) ||
                    voucher.code?.contains(query, ignoreCase = true) == true
        }
        if(filteredVouchers.isNotEmpty()){
            updateItemSelection(filteredVouchers[0].id,true)
        }
        else{
            selectedVoucherId=null
            view?.updateTvDiscountPrice(null)
        }
        view?.updateVoucherList(filteredVouchers, selectedVoucherId)
    }

    override fun setHasSelectedProducts(hasSelected: Boolean) {
        hasSelectedProducts = hasSelected
    }

    override fun onAgreeButtonClick() {
        val selectedVoucher = selectedVoucherId?.let {
            voucherId -> allVouchers.find {
                it.id == voucherId
            }
        }
        view?.finishWithResult(selectedVoucher)
    }
}