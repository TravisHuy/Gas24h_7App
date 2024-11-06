package com.nhathuy.gas24h_7app.admin.notification.all_notification

import com.nhathuy.gas24h_7app.data.model.Notification

interface AllNotificationContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
        fun showAllNotifications(notifications:List<Notification>)
        fun showDialogDeleteNotification()
        fun navigateEditNotification()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadNotifications()
        fun deleteNotification(id:String)
    }
}