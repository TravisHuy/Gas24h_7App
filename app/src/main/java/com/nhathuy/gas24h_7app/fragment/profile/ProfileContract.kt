package com.nhathuy.gas24h_7app.fragment.profile

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User

interface ProfileContract {
    interface View{
        fun navigatePurchaseOrder()
        fun showUpdateProfileImage(imageUrl:String)
        fun showError(message: String)
        fun showLoading(isLoading: Boolean)
        fun showMessage(message: String)
        fun showUserInfo(isLoggedIn:Boolean)
        fun updateProfileImage(imageUrl: String)
        fun updateUserName(name:String)
        fun updateCartItemCount(count: Int)
        fun updateOrderCount( processingCount: Int,pendingCount: Int, shippedCount: Int, deliveredCount: Int)
        fun showDialogLogout()
        fun showOrders(orders:List<Order>, products:Map<String, Product>)
        fun navigateBuyBack()
        fun navigateCart()

    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun updateProfileImage(imageUri: Uri)

        fun loadUserInfo()
        fun loadOrders(status:String)
        fun loadCartItemCount()
        fun loadOrderCount()
    }
}