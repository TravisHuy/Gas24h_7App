package com.nhathuy.gas24h_7app.ui.detail_notification

import com.nhathuy.gas24h_7app.data.model.Notification

interface DetailNotificationContract {
    interface View{
        fun showNotification(notification: Notification)
        fun showError(message:String)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadNotificationDetail(id:String)
    }
}