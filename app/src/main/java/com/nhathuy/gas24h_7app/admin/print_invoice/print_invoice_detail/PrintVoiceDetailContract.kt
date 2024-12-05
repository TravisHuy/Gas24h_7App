package com.nhathuy.gas24h_7app.admin.print_invoice.print_invoice_detail

import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User

interface PrintVoiceDetailContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun showMessage(message: String)
        fun showOrders(orders:List<Order>, products:Map<String, Product>, users:Map<String, User>)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun printVoices(orderIds:List<String>)
    }
}