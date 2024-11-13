package com.nhathuy.gas24h_7app.admin.chat.message

import com.nhathuy.gas24h_7app.data.model.ChatRoom
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.MessageType

interface ChatMessageAdminContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessages(messages: List<Message>)
        fun showMessageSent(message: Message)
        fun showError(message: String)
        fun scrollToBottom()
        fun showImagePicker()
        fun updateChatRoom(chatRoom: ChatRoom)
        fun updateCurrentAdminId(adminId: String)
        fun clearInput()
        fun updateUserInfo(name: String, avatar: String?)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun initialize(chatRoomId:String?)
        fun loadMessages()
        fun sendMessage(content: String, type: MessageType = MessageType.TEXT)
        fun sendMessageImage(mediaUrl: String, type: MessageType = MessageType.IMAGE)
        fun sendImage(uri: android.net.Uri)
        fun markMessagesAsRead()
        fun onImagePickerClicked()
        fun cleanup()
    }
}