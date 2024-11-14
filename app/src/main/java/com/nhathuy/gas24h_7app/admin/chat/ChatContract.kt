package com.nhathuy.gas24h_7app.admin.chat

import com.nhathuy.gas24h_7app.data.model.ChatRoom
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.User

interface ChatContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun displayRooms(rooms:List<ChatRoom>)
        fun updateChatRoom(messages:Map<String,Message>,users:Map<String,User>)
        fun showEmpty()
        fun onAdminIdLoaded(adminId: String)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadRecentChat()
        fun loadMessages(chatRoomId:String)
        fun loadUserDetails(userId: String)
        fun markAsRead(chatRoomId: String, userId: String)
    }
}