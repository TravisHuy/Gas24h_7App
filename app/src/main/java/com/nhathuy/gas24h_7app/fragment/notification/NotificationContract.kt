package com.nhathuy.gas24h_7app.fragment.notification

import com.nhathuy.gas24h_7app.data.model.Notification

interface NotificationContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
        fun showNotifications(notifications:List<Notification>)
        fun navigateDetailNotification(id:String)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadNotifications()
    }
}