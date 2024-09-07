package com.nhathuy.gas24h_7app.ui.order

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.DiscountType
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.data.repository.VoucherRepository
import com.nhathuy.gas24h_7app.ui.cart.CartContract
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderPresenter @Inject constructor(private val userRepository: UserRepository,
                                         private val productRepository: ProductRepository,
                                        private val voucherRepository: VoucherRepository):OrderContract.Presenter{
    private var view:OrderContract.View? = null
    private var orderItems= mutableListOf<CartItem>()
    private val products = mutableMapOf<String,Product>()

    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    //
    private var appliedVoucher : Voucher? =null
    private var currentVoucherId: String? =null

    override fun attachView(view: OrderContract.View) {
        this.view=view
    }

    override fun detachView() {
        view = null
        job.cancel() // Cancel the coroutine scope when detaching the view
    }

    override fun loadOrderItems(selectedItems: MutableList<CartItem>) {
        orderItems = selectedItems // Update the orderItems list
        coroutineScope.launch {
            selectedItems.forEach { cartItem ->
                val productResult = productRepository.getProductById(cartItem.productId)
                if (productResult.isSuccess) {
                    products[cartItem.productId] = productResult.getOrNull()!!
                } else {
                    view?.showError("Failed to load product: ${cartItem.productId}")
                }
            }
            view?.showOrderItems(orderItems, products)
        }
    }
    override fun setInitialVoucher(voucherId: String, discount: Double, discountType: String?) {
        applyVoucher(voucherId, discount, discountType)
    }


    override fun applyVoucher(voucherId: String, discount: Double, discountType: String?) {
        coroutineScope.launch {
            val voucher  = voucherRepository.getVoucherById(voucherId)
            voucher.fold(
                onSuccess = { retrievedVoucher ->
                    appliedVoucher= retrievedVoucher
                    currentVoucherId=retrievedVoucher.id
                    updateVoucherInfo()
                },
                onFailure = {
                    view?.showError("Voucher không áp dụng được cho đơn hàng này")
                }
            )
        }
    }

    private fun updateVoucherInfo() {
        appliedVoucher?.let {
            voucher ->
            val discountInfo = formatVoucherDiscount(voucher)
            view?.updateVoucherInfo("-$discountInfo")
        }
    }

    override fun getCurrentVoucherId(): String? {
        return currentVoucherId
    }

    override fun removeVoucher() {
        appliedVoucher= null
        currentVoucherId = null
        view?.updateVoucherInfo(null)
    }


    private fun formatVoucherDiscount(voucher: Voucher): String {
        return when (voucher.discountType) {
            DiscountType.FIXED_AMOUNT -> "${NumberFormatUtils.formatDiscount(voucher.discountValue)}"
            DiscountType.PERCENTAGE -> "${voucher.discountValue.toInt()}%"
        }
    }

}