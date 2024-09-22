package com.nhathuy.gas24h_7app.ui.order

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.ApplicableType
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.DiscountType
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderItem
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.data.repository.CartRepository
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.data.repository.VoucherRepository
import com.nhathuy.gas24h_7app.ui.cart.CartContract
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class OrderPresenter @Inject constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val voucherRepository: VoucherRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {
    private var view: OrderContract.View? = null
    private var orderItems = mutableListOf<CartItem>()
    private val products = mutableMapOf<String, Product>()

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    //
    private var appliedVoucher: Voucher? = null
    private var currentVoucherId: String? = null

    //
    private var totalAmount: Double = 0.0

    //
    private var currentUser: User? = null
    override fun attachView(view: OrderContract.View) {
        this.view = view
        loadUserInfo()
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
            calculateTotalAmount()
            updateTotalAmountUI()
        }
    }


    override fun setInitialVoucher(voucherId: String, discount: Double, discountType: String?) {
        applyVoucher(voucherId, discount, discountType)
    }


    override fun applyVoucher(voucherId: String, discount: Double, discountType: String?) {
        coroutineScope.launch {
            val voucher = voucherRepository.getVoucherById(voucherId)
            voucher.fold(
                onSuccess = { retrievedVoucher ->
                    if (isVoucherApplicable(retrievedVoucher)) {
                        if (isTotalAmountEligibleForVoucher(retrievedVoucher)) {
                            appliedVoucher = retrievedVoucher
                            currentVoucherId = retrievedVoucher.id
                            updateVoucherInfo()
                            updateTotalAmountUI()
                        } else {
                            view?.showError("Tổng giá trị đơn hàng chưa đủ để áp dụng voucher này")
                        }
                    } else {
                        view?.showError("Voucher không áp dụng được cho đơn hàng này")
                    }
                },
                onFailure = {
                    view?.showError("Voucher không áp dụng được cho đơn hàng này")
                }
            )
        }
    }

    private fun isVoucherApplicable(voucher: Voucher): Boolean {
        return when (voucher.applicableType) {
            ApplicableType.ALL_PRODUCTS -> true
            ApplicableType.DETAIL_PRODUCTS -> orderItems.any { cartItem ->
                voucher.applicableProductIds.contains(cartItem.productId)
            }
        }
    }

    private fun isTotalAmountEligibleForVoucher(voucher: Voucher): Boolean {
        return calculateTotalAmount() >= voucher.minOrderAmount
    }

    private fun updateVoucherInfo() {
        appliedVoucher?.let { voucher ->
            val discountInfo = formatVoucherDiscount(voucher)
            view?.updateVoucherInfo("-$discountInfo")
        }
    }

    override fun getCurrentVoucherId(): String? {
        return currentVoucherId
    }

    override fun getCurrentUserId(): String? {
        return userRepository.getCurrentUserId()
    }

    override fun loadUserInfo() {
        coroutineScope.launch {
            val result = userRepository.getUser(userRepository.getCurrentUserId()!!)
            result.fold(
                onSuccess = { user ->
                    currentUser = user
                    view?.showUserInfo(user)
                },
                onFailure = {
                    view?.showError("Failed to load user information")
                }
            )
        }
    }

    override fun removeVoucher() {
        appliedVoucher = null
        currentVoucherId = null
        view?.updateVoucherInfo(null)
        updateTotalAmountUI()
    }

    override fun placeOrder() {
        coroutineScope.launch {
            val userId = userRepository.getCurrentUserId() ?: return@launch

            val order = Order(
                id = UUID.randomUUID().toString(),
                userId = userId,
                items = orderItems.map {
                    OrderItem(
                        it.productId,
                        it.quantity,
                        products[it.productId]?.price ?: 0.0
                    )
                },
                totalAmount = totalAmount,
                discountAmount = totalAmount - calculateDiscountedAmount(),
                appliedVoucherId = currentVoucherId,
                status = OrderStatus.PENDING,
                createdAt = Date(),
                updatedAt = Date(),
                shippingAddress = currentUser?.address ?: "",
                paymentMethod = "Thanh toán khi nhận hàng"
            )

            val result = orderRepository.createOrder(order)

            if (result.isSuccess) {

                orderItems.forEach { item ->
                    cartRepository.removeCartItem(userId, item.productId)
                }

                currentVoucherId?.let { voucherId ->
                    val voucherUpdateResult =
                        voucherRepository.updateVoucherUsage(voucherId, userId)
                    when {
                        voucherUpdateResult.isSuccess -> {
                            val remainingUsages = voucherUpdateResult.getOrNull()
                            remainingUsages?.let {
                                view?.showRemainingVoucherUsages(it)
                            }
                        }

                        else -> {
                            view?.showError("Failed to update voucher usage")
                            return@launch
                        }
                    }
                }

                view?.showSuccess()
                view?.navigatePendingPayment()
            } else {
                view?.showError(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }


    private fun formatVoucherDiscount(voucher: Voucher): String {
        return when (voucher.discountType) {
            DiscountType.FIXED_AMOUNT -> "${NumberFormatUtils.formatDiscount(voucher.discountValue)}"
            DiscountType.PERCENTAGE -> "${voucher.discountValue.toInt()}%"
        }
    }

    private fun updateTotalAmountUI() {
        val originalAmount = calculateTotalAmount()
        val discountedAmount = calculateDiscountedAmount()
        view?.updateTotalAmount(originalAmount, discountedAmount)
    }

    fun calculateDiscountedAmount(): Double {
        return appliedVoucher?.let { voucher ->
            val eligibleAmount = when (voucher.applicableType) {
                ApplicableType.ALL_PRODUCTS -> totalAmount
                ApplicableType.DETAIL_PRODUCTS -> calculateEligibleAmountForDetailProducts(voucher)
            }

            val discountAmount = when (voucher.discountType) {
                DiscountType.FIXED_AMOUNT -> voucher.discountValue
                DiscountType.PERCENTAGE -> eligibleAmount * (voucher.discountValue / 100)
            }

            (totalAmount - discountAmount).coerceAtLeast(0.0)
        } ?: totalAmount
    }

    private fun calculateEligibleAmountForDetailProducts(voucher: Voucher): Double {
        return orderItems.sumOf { cartItem ->
            if (voucher.applicableProductIds.contains(cartItem.productId)) {
                (products[cartItem.productId]?.price ?: 0.0) * cartItem.quantity
            } else {
                0.0
            }
        }
    }

    private fun calculateTotalAmount(): Double {
        totalAmount = orderItems.sumOf { cartItem ->
            val product = products[cartItem.productId]
            (product?.price ?: 0.0) * cartItem.quantity
        }
        return totalAmount
    }
}