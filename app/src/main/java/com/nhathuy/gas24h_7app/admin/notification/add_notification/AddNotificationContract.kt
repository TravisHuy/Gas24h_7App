package com.nhathuy.gas24h_7app.admin.notification.add_notification

import android.net.Uri

interface AddNotificationContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
        fun showImageError()
        fun clear()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun addNotification(title:String, content:String,imageUri:Uri? ,hotline:String)
    }
}