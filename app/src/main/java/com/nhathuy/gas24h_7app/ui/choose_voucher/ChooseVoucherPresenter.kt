package com.nhathuy.gas24h_7app.ui.choose_voucher

import android.util.Log
import com.nhathuy.gas24h_7app.data.repository.VoucherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChooseVoucherPresenter @Inject constructor(private val voucherRepository: VoucherRepository):ChooseVoucherContract.Presenter {

    private var view:ChooseVoucherContract.View?=null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    private val selectedVouchers = mutableSetOf<String>()

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
                    view?.updateVoucherList(vouchers,selectedVouchers)
                },
                onFailure = {e ->
                    view?.showError("Failed to voucher: ${e.message}")
                    Log.d("CHooseActivity","${e.message}")
                }
            )
        }
    }

    override fun searchVouchers() {

    }

    override fun updateItemSelection(voucherId: String, isChecked: Boolean) {
        if(isChecked){
            selectedVouchers.add(voucherId)
        }
        else{
            selectedVouchers.remove(voucherId)
        }
    }
}