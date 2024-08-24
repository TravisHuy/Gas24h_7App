package com.nhathuy.gas24h_7app.admin.voucher.detail_product

import com.nhathuy.gas24h_7app.admin.voucher.all_product.VoucherAllContract
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.VoucherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

class VoucherDetailPresenter @Inject constructor(private val voucherRepository: VoucherRepository,
                                                 private val productRepository: ProductRepository):VoucherDetailContract.Presenter {

    private var view:VoucherDetailContract.View?=null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    private var startDate:Calendar = Calendar.getInstance()
    private var endDate:Calendar = Calendar.getInstance()

    private val selectedProducts = mutableSetOf<String>()
    private var allProducts = listOf<Product>()

    private var isAllSelected =false
    override fun attachView(view: VoucherDetailContract.View) {
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
                val voucherWithSelectProducts = voucher.copy(applicableProductIds = selectedProducts.toList(), isForAllProducts = isAllSelected)
                val result=voucherRepository.createVoucher(voucherWithSelectProducts)
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

    override fun loadProducts() {
        coroutineScope.launch {
            view?.showLoading()
            val result =productRepository.getAllProducts()
            result.fold(
                onSuccess = {
                    products ->
                    allProducts=products
                    view?.updateProductList(products,selectedProducts)
                },
                onFailure = {
                    e->
                    view?.showError("Failed to load products: ${e.message}")
                }
            )
            view?.hideLoading()
        }
    }

    override fun searchProducts(query: String) {
        val filteredProducts= allProducts.filter {
            it.name.contains(query,ignoreCase = false)
        }
        view?.updateProductList(filteredProducts,selectedProducts)
    }

    override fun getSelectedProducts(): List<Product> {
        return allProducts.filter {
                it.id in selectedProducts
        }
    }

    override fun updateItemSelection(productId: String, isChecked: Boolean) {
        if (isChecked){
            selectedProducts.add(productId)
        }
        else{
            selectedProducts.remove(productId)
        }
        isAllSelected = selectedProducts.size == allProducts.size
        view?.updateSelectAllCheckbox(isAllSelected)
    }

    override fun toggleSelectAll(isChecked: Boolean) {
        isAllSelected = isChecked
        if (isChecked) {
            selectedProducts.addAll(allProducts.map { it.id })
        } else {
            selectedProducts.clear()
        }
        view?.updateProductList(allProducts, selectedProducts)
    }

    private fun isValidVoucherCode(code: String):Boolean {
        return code.matches(Regex("[A-Z0-9]{1,5}$"))
    }
}