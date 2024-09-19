package com.nhathuy.gas24h_7app.admin.order.shipping

import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User

interface ShippingContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun showMessage(message: String)
        fun showOrders(orders:List<Order>, products:Map<String, Product>, users:Map<String, User>)
        fun updateOrderList(orders: List<Order>, selectedOrders: MutableSet<String>)
        fun updateSelectAllCheckbox(isAllSelected:Boolean)
        fun clearSelectItems()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadOrders(status:String)
        fun getSelectedOrders(): List<Order>
        fun  updateItemSelection(orderId:String,isChecked:Boolean)
        fun toggleSelectAll(isChecked: Boolean)

        fun searchOrders(query:String)
        fun confirmSelectOrders()
        fun clearSelection()
    }
}