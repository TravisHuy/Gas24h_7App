package com.nhathuy.gas24h_7app.admin.voucher.all_product

import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.data.repository.VoucherRepository
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Calendar
import javax.inject.Inject

class VoucherAllPresenter @Inject constructor(private val voucherRepository: VoucherRepository):VoucherAllContract.Presenter {

    private var view: VoucherAllContract.View?=null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    private var startDate:Calendar = Calendar.getInstance()
    private var endDate:Calendar = Calendar.getInstance()


    override fun attachView(view: VoucherAllContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
        job.cancel()
    }

    override fun createVoucher(voucher: Voucher) {
        if(validateVoucherInput(voucher)){
            coroutineScope.launch {
                view?.showLoading()
                val result=voucherRepository.createVoucher(voucher)
                result.fold(
                    onSuccess = {
                        view?.showSuccess("Voucher created successfully")
                        view?.clearFields()
                    },
                    onFailure = { e ->
                        view?.showError("Failed to create voucher: ${e.message}")
                    }
                )
                view?.hideLoading()
            }
        }
    }

    override fun validateVoucherInput(voucher: Voucher): Boolean {
        when{
            !isValidVoucherCode(voucher.code) -> {
                view?.showError("Invalid voucher code. Must be 1-5 character , only A-Z and 0-9")
                return false
            }
            voucher.discountValue <= 0 ->{
                view?.showError("Discount value must be greater than zero")
            }
            voucher.minOrderAmount < 0 ->{
                view?.showError("Minimum order amount cannot be negative")
            }
            voucher.maxUsage <= 0 -> {
                view?.showError("Maximum usage must be greater than zero")
                return false
            }
            voucher.maxUsagePreUser <= 0 -> {
                view?.showError("Maximum usage per user must be greater than zero")
                return false
            }
            startDate.after(endDate) -> {
                view?.showError("Start date must be before end date")
                return false
            }
        }
        return true
    }

    private fun isValidVoucherCode(code: String): Boolean{
        return code.matches(Regex("[A-Z0-9]{1,5}$"))
    }

    override fun onStartDateSelected(year: Int, month: Int, day: Int) {
        startDate.set(year,month,day)
        view?.updateDateTimeDisplay(startDate,true)
    }

    override fun onStartTimeSelected(hourOfDay: Int, minute: Int) {
        startDate.set(Calendar.HOUR_OF_DAY,hourOfDay)
        startDate.set(Calendar.MINUTE,minute)
        view?.updateDateTimeDisplay(startDate,true)
    }

    override fun onEndDateSelected(year: Int, month: Int, day: Int) {
        endDate.set(year,month,day)
        view?.updateDateTimeDisplay(endDate,false)
    }

    override fun onEndTimeSelected(hourOfDay: Int, minute: Int) {
        endDate.set(Calendar.HOUR_OF_DAY,hourOfDay)
        endDate.set(Calendar.MINUTE,minute)
        view?.updateDateTimeDisplay(endDate,false)
    }
    override fun getStartDate(): Calendar = startDate
    override fun getEndDate(): Calendar = endDate

    override fun resetDates() {
        startDate = Calendar.getInstance()
        endDate = Calendar.getInstance()
        view?.updateDateTimeDisplay(startDate, true)
        view?.updateDateTimeDisplay(endDate, false)
    }
}