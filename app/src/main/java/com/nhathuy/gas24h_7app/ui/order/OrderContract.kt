package com.nhathuy.gas24h_7app.ui.order

import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.ui.cart.CartContract

interface OrderContract {
    interface View{
        fun showOrderItems(items: MutableList<CartItem>, products: MutableMap<String, Product>)
        fun showError(message: String)
        fun updateVoucherInfo(price: String?)
        fun updateTotalAmount(totalAmount: Double, discountedAmount: Double)
        fun showUserInfo(user:User)
        fun showSuccess()
        fun showRemainingVoucherUsages(remainingUsages:Int)
        fun navigateHome()
        fun navigateSalesPolicy()
    }
    interface Presenter{
        fun attachView(view: OrderContract.View)
        fun detachView()
        fun loadOrderItems(selectedItems: MutableList<CartItem>)
        fun setInitialVoucher(voucherId: String, discount: Double, discountType: String?)
        fun applyVoucher(voucherId: String, discount: Double, discountType: String?)
        fun getCurrentVoucherId(): String?
        fun getCurrentUserId(): String?
        fun loadUserInfo()

        fun removeVoucher()
        fun placeOrder()
    }
}
