package com.nhathuy.gas24h_7app.ui.chat_message

import android.net.Uri
import android.util.Log
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.MessageType
import com.nhathuy.gas24h_7app.data.repository.ChatRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ChatMessagePresenter @Inject constructor(private val chatRepository: ChatRepository,
                                               private val userRepository: UserRepository
):ChatMessageContract.Presenter {


    private var view:ChatMessageContract.View? = null
    private var chatRoomId: String? = null
    private var currentUserId: String? = null
    private var adminId: String? = null

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)


    override fun attachView(view: ChatMessageContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun initialize(orderId: String?) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                view?.showLoading()

                currentUserId = userRepository.getCurrentUserId()  ?: throw  Exception("User not logged in")

                adminId = userRepository.getUserAdminId().getOrNull() ?: throw Exception("Admin not found")


                val result = chatRepository.getOrCreateChatRoom(sellerId = adminId!!, buyerId = currentUserId!!)

                result.fold(
                    onSuccess = {room ->
                        withContext(Dispatchers.Main){
                            chatRoomId = room.id
                            view?.updateChatRoom(room)
                            loadMessages()
                            markMessagesAsRead()
                        }
                    },
                    onFailure = {e->
                        view?.showError("Failed to initialize chat: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showError("Error initializing chat: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun loadMessages() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                chatRoomId?.let { roomId->
                    chatRepository.getMessages(roomId)
                        .collect {
                                messages ->
                            withContext(Dispatchers.Main){
                                view?.showMessages(messages.sortedBy { it.timestamp })
                                view?.scrollToBottom()
                            }
                        }
                }

            }
            catch (e:Exception){
                withContext(Dispatchers.Main){
                    view?.showError("Failed to load messages: ${e.message}")
                }
            }
            finally {
                view?.hideLoading()
            }
        }
    }


    override fun sendMessage(content: String, type: MessageType) {
        if(content.isBlank() && type == MessageType.TEXT) return

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val message = Message(
                    id = UUID.randomUUID().toString(),
                    chatRoomId = chatRoomId?: return@launch,
                    senderId = currentUserId ?:return@launch,
                    receiverId = adminId ?:return@launch,
                    content = content,
                    type = type,
                    timestamp = System.currentTimeMillis()
                )

                val result = chatRepository.sendMessage(message)

                result.fold(
                    onSuccess = {
                        withContext(Dispatchers.Main){
//                            view?.showMessageSent(message)
                            view?.clearInput()
                        }
                    },
                    onFailure = {e->
                        withContext(Dispatchers.Main){
                            view?.showError("Failed to send messages: ${e.message}")
                        }
                    }
                )
            }
            catch (e:Exception){
                withContext(Dispatchers.Main){
                    view?.showError("Failed to send message: ${e.message}")
                }
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun sendImage(uri: Uri) {
        coroutineScope.launch {
            try {
                val uploadResult = chatRepository.uploadImage(uri)

                uploadResult.fold(
                    onSuccess = {
                        imageUrl ->
                        sendMessage(imageUrl,MessageType.IMAGE)
                    },
                    onFailure = { e->
                        view?.showError("Failed to upload image: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showError("Error marking messages as read: ${e.message}")
            }
        }
    }

    override fun markMessagesAsRead() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                chatRoomId?.let {
                    roomId->
                    currentUserId?.let {
                        userId ->
                        chatRepository.markMessageAsRead(roomId,userId)
                    }
                }
            }
            catch (e:Exception){
                view?.showError("Error marking messages as read: ${e.message}")
            }
        }
    }

    override fun onImagePickerClicked() {
        view?.showImagePicker()
    }

    override fun cleanup() {
        coroutineScope.cancel()
    }


}