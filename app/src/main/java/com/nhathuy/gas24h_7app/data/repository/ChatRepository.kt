package com.nhathuy.gas24h_7app.data.repository

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.ChatRoom
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.MessageStatus
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(message:Message) : Result<Unit>
    suspend fun getMessages(chatRoomId:String) : Flow<List<Message>>
    suspend fun findExistingChatRoom(sellerId:String,buyerId:String):ChatRoom?
    suspend fun getOrCreateChatRoom(sellerId:String,buyerId:String):Result<ChatRoom>
    suspend fun updateMessageStatus(messageId:String,status: MessageStatus):Result<Unit>
    suspend fun getUnreadCount(chatRoomId: String,userId:String):Flow<Int>
    suspend fun uploadImage(uri:Uri):Result<String>
    suspend fun getRecentChats(userId: String):Flow<List<ChatRoom>>
    suspend fun markMessageAsRead(chatRoomId: String,userId: String):Result<Unit>
}