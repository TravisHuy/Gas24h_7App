package com.nhathuy.gas24h_7app.admin.voucher.detail_product

import com.nhathuy.gas24h_7app.admin.voucher.all_product.VoucherAllContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class VoucherDetailPresenter @Inject constructor():VoucherDetailContract.Presenter {

    private var view:VoucherDetailContract.View?=null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    override fun attachView(view: VoucherDetailContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
        job.cancel()
    }

}