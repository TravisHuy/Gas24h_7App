package com.nhathuy.gas24h_7app.ui.cart

import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.DiscountType
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductContract

interface CartContract {
    interface View{
        fun showCartItems(cartItems: List<CartItem>, products: Map<String, Product>)
        fun showError(message: String)
        fun updateCartItemQuantity(productId: String, newQuantity: Int)
        fun updateSelectedItems(selectedIds: Set<String>)
        fun updateTotalPrice(total:Double)
        fun updatePurchaseBtnText(count:Int)
        fun updateAllItemsSelection(isChecked: Boolean)
        fun showStockExceededError(productId: String, maxQuantity: Int)
        fun showCartSize(size:Int)
        fun navigateToCheckout(selectedItems: List<CartItem>, totalAmount: Double)
        fun updateVoucherInfo(price:String?)
    }
    interface Presenter{
        fun attachView(view: CartContract.View)
        fun detachView()
        fun loadCartItems()
        fun updateCartItemQuantity(productId: String, newQuantity: Int)
        fun updateItemSelection(productId:String,isChecked:Boolean)
        fun calculateTotalPrice()
        fun selectAllItems(isChecked: Boolean)
        fun updateAllItemsSelection(isChecked: Boolean)
        fun handleManualQuantityExceeded(productId: String, maxQuantity: Int)
        fun onBtnClicked()
        fun hasSelectedItems(): Boolean
        fun applyVoucher(voucherId: String)
        fun getCurrentVoucherId(): String?
        fun removeVoucher()
        fun getAppliledVoucherDiscount(): Double
        fun getAppliledVoucherDiscountType(): DiscountType?
    }
}