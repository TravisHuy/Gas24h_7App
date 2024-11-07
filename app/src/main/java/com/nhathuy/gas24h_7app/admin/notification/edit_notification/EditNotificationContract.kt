package com.nhathuy.gas24h_7app.admin.notification.edit_notification

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.Notification
interface EditNotificationContract {
    interface View {
        var title: String
        var content: String
        var hotline: String

        fun showLoading()
        fun hideLoading()
        fun showSuccess(message: String)
        fun showMessage(message: String)
        fun showNotificationData(notification: Notification)
        fun showTitleError(message: String?)
        fun showContentError(message: String?)
        fun showHotlineError(message: String?)
        fun showImageError()
        fun navigateAllNotification()
        fun clear()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadNotification(id: String)
        fun editNotification(id: String, imageUri: Uri?)
    }
}