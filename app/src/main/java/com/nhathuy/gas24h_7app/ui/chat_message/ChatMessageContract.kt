package com.nhathuy.gas24h_7app.ui.chat_message

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.ChatRoom
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.MessageStatus
import com.nhathuy.gas24h_7app.data.model.MessageType
import com.nhathuy.gas24h_7app.data.model.UserStatus

interface ChatMessageContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessages(messages: List<Message>)
        fun showMessageSent(message: Message)
        fun showError(message: String)
        fun scrollToBottom()
        fun showImagePicker()
        fun showAdminId(adminId:String)
        fun updateChatRoom(chatRoom: ChatRoom)
        fun clearInput()
        fun updateOnlineStatus(status: UserStatus)
        fun updateParticipantsStatus(statusMap: Map<String, UserStatus>)
    }
    interface Presenter{
        fun attachView(view: View)
        fun detachView()
        fun initialize(orderId: String? = null)
        fun loadMessages()
        fun sendMessage(content: String, type: MessageType= MessageType.TEXT)
        fun sendMessageImage(content: String, type: MessageType= MessageType.IMAGE)
        fun sendImage(uri: Uri)
        fun markMessagesAsRead()
        fun onImagePickerClicked()
        fun onResume()
        fun onPause()
        fun cleanup()
    }
}