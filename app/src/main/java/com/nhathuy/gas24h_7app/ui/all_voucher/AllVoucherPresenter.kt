package com.nhathuy.gas24h_7app.ui.all_voucher

import com.nhathuy.gas24h_7app.data.repository.VoucherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class AllVoucherPresenter @Inject constructor(private val voucherRepository: VoucherRepository):AllVoucherContract.Presenter {

    private var view:AllVoucherContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun attachView(view: AllVoucherContract.View) {
        this.view=view
    }

    override fun detachView() {
        view = null
    }

    override fun loadVouchers() {
        coroutineScope.launch {
            view?.showLoading()
            try {
                val result = voucherRepository.getAllVouchers()
                result.fold(
                    onSuccess = { vouchers->
                        view?.showVoucher(vouchers)
                    },
                    onFailure = { e->
                        view?.showError("Failed load voucher: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showError("Failed load voucher: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }
}