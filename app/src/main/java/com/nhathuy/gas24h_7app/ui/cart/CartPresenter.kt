package com.nhathuy.gas24h_7app.ui.cart

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Cart
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.DiscountType
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.data.repository.CartRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.data.repository.VoucherRepository
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductContract
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartPresenter @Inject constructor(private val cartRepository: CartRepository, private val userRepository: UserRepository,private val voucherRepository: VoucherRepository):CartContract.Presenter{

    private var view:CartContract.View?=null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    private var cartItems: List<CartItem> = emptyList()
    private var products : Map<String,Product> = emptyMap()
    private val selectItemIds = mutableSetOf<String>()

    private var appliedVoucher : Voucher? =null
    private var currentVoucherId: String? =null

    override fun attachView(view: CartContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
        job.cancel()
    }

    override fun loadCartItems() {
        coroutineScope.launch {
            try {
                val userId= userRepository.getCurrentUserId()
                Log.d("CartPresenter", "Loading cart for user: $userId")
                val cartResult =cartRepository.getCart(userId!!)
                cartResult.fold(
                    onSuccess ={cart ->
                        Log.d("CartPresenter", "Cart object: ${cart}")
                        cartItems =cart.items
                        val productIds= cart.items.map {
                            it.productId
                        }
                        val productResult = cartRepository.getProductForCart(productIds)
                        productResult.fold(
                            onSuccess = { loadedProducts ->
                                products= loadedProducts
                                view?.showCartItems(cart.items,products)
                                view?.showCartSize(cartItems.size)
                                calculateTotalPrice()
                            },
                            onFailure = {
                                    e ->
                                Log.e("CartPresenter", "Error loading products", e)
                                view?.showError("Failed to load product details: ${e.message}")

                            }
                        )
                    },
                    onFailure = {e ->
                        Log.d("CartPresenter", "Cart is empty or null",e)
                        view?.showCartItems(emptyList(), emptyMap())
                    }
                )
            }
            catch (e:Exception){
                Log.e("CartPresenter", "Error loading cart items", e)
                view?.showError("Failed to load cart items: ${e.message}")
            }
        }
    }


    override fun updateCartItemQuantity(productId: String, newQuantity: Int) {
        coroutineScope.launch {
            try {
                val userId = userRepository.getCurrentUserId()
                val result = cartRepository.updateCartItemQuantity(userId!!,productId,newQuantity)

                result.fold(
                    onSuccess = {
                        view?.updateCartItemQuantity(productId,newQuantity)
                        loadCartItems()
                    },
                    onFailure = { e ->
                        view?.showError("Failed to update quantity: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                view?.showError("Failed to update quantity: ${e.message}")
            }
        }
    }

    override fun updateItemSelection(productId: String, isChecked: Boolean) {
        if(isChecked){
            selectItemIds.add(productId)
        }
        else{
            selectItemIds.remove(productId)
        }
        calculateTotalPrice()
    }

    override fun calculateTotalPrice() {
        var total =cartItems.filter { it.productId in selectItemIds }.sumOf { cartItem ->
            val product= products[cartItem.productId]
            product?.let {
                if(it.offerPercentage>0.0){
                    (it.getDiscountedPrice())*cartItem.quantity
                }
                else{
                    it.price*cartItem.quantity
                }
            }?:0.0
        }

        appliedVoucher?.let {voucher ->
            when(voucher.discountType){
                DiscountType.FIXED_AMOUNT -> total -=voucher.discountValue
                DiscountType.PERCENTAGE -> total *= (1 - voucher.discountValue / 100)
            }
        }
        Log.d("CartPresenter", "Total price calculated: $total")
        view?.updateTotalPrice(total)
        view?.updatePurchaseBtnText(selectItemIds.size)
    }

    override fun selectAllItems(isChecked: Boolean) {
        if(isChecked){
            selectItemIds.addAll(cartItems.map { it.productId })
        }
        else{
            selectItemIds.clear()
        }
        view?.updateSelectedItems(selectItemIds)
        calculateTotalPrice()
    }

    override fun updateAllItemsSelection(isChecked: Boolean) {
        selectAllItems(isChecked)
        view?.updateAllItemsSelection(isChecked)
    }

    override fun handleManualQuantityExceeded(productId: String, maxQuantity: Int) {
        view?.showStockExceededError(productId, maxQuantity)
        updateCartItemQuantity(productId, maxQuantity)
    }

    override fun onBtnClicked() {
        val selectItems = cartItems.filter { it.productId in selectItemIds }
        val totalAmount =calculateTotalAmount(selectItems)
        view?.navigateToCheckout(selectItems, totalAmount)
    }

    override fun hasSelectedItems(): Boolean {
       return cartItems.isNotEmpty()
    }


    override fun applyVoucher(voucherId: String) {
        coroutineScope.launch {
            val voucher = voucherRepository.getVoucherById(voucherId)
            voucher.fold(
                onSuccess ={retrievedVoucher ->
                    appliedVoucher= retrievedVoucher
                    currentVoucherId=retrievedVoucher.id
                    calculateTotalPrice()
                    val discountInfo = formatVoucherDiscount(retrievedVoucher)
                    view?.updateVoucherInfo("-${discountInfo}")
                },
                onFailure = {
                    view?.showError("Voucher không áp dụng được cho đơn hàng này")
                }
            )
        }
    }

    private fun formatVoucherDiscount(voucher: Voucher): String {
        return when (voucher.discountType) {
            DiscountType.FIXED_AMOUNT -> "${NumberFormatUtils.formatDiscount(voucher.discountValue)}"
            DiscountType.PERCENTAGE -> "${voucher.discountValue.toInt()}%"
        }
    }

    override fun getCurrentVoucherId(): String? {
        return currentVoucherId
    }

    override fun removeVoucher() {
        appliedVoucher= null
        currentVoucherId = null
        calculateTotalPrice()
        view?.updateVoucherInfo(null)
    }

    override fun getAppliledVoucherDiscount(): Double {
        return appliedVoucher?.discountValue ?: 0.0
    }

    override fun getAppliledVoucherDiscountType(): DiscountType? {
        return appliedVoucher?.discountType
    }

    private fun calculateTotalAmount(items: List<CartItem>): Double {
        return items.sumOf {
            cartItem ->
            val product=products[cartItem.productId]
            product?.let {
                if(it.offerPercentage>0.0){
                    (it.getDiscountedPrice())*cartItem.quantity
                }
                else{
                    it.price*cartItem.quantity
                }
            }?:0.0
        }
    }


}