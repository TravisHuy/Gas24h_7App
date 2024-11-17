package com.nhathuy.gas24h_7app.admin.chat.message

import android.net.Uri
import android.util.Log
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.MessageType
import com.nhathuy.gas24h_7app.data.model.UserStatus
import com.nhathuy.gas24h_7app.data.repository.ChatRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ChatMessageAdminPresenter @Inject constructor(private val chatRepository: ChatRepository,
                                                    private val userRepository: UserRepository): ChatMessageAdminContract.Presenter {

    private var view:ChatMessageAdminContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private var onlineStatusJob: Job? = null
    private var messagesJob: Job? = null

    private var chatRoomId: String? = null
    private var currentAdminId: String? = null
    private var buyerId: String? = null
    private var isInitialized = false
    override fun attachView(view: ChatMessageAdminContract.View) {
        this.view = view
    }

    override fun detachView() {
        cleanup()
        coroutineScope.cancel() // Cancel coroutineScope only when detaching view
        view = null
    }

    override fun initialize(buyerId:String?) {
        if (buyerId == null) {
            view?.showError("Invalid buyer ID")
            return
        }

        // Reset state when initializing
        cleanup()
        this.buyerId = buyerId
        isInitialized = false

        coroutineScope.launch {
            try {
                view?.showLoading()

                currentAdminId = userRepository.getCurrentUserId() ?:  throw Exception("Admin not logged in")
                this@ChatMessageAdminPresenter.buyerId = buyerId
                view?.updateCurrentAdminId(currentAdminId!!)


                messagesJob?.cancel()
                onlineStatusJob?.cancel()


                // load user info
                val userInfo = userRepository.getUser(buyerId!!)
                userInfo.fold(
                    onSuccess = {
                        user ->
                        withContext(Dispatchers.Main) {
                            view?.updateUserInfo(user.fullName, user.imageUser)
                        }
                    },
                    onFailure = {
                        e ->
                        view?.showError("Information user error : ${e.message}")
                    }
                )


                val result = chatRepository.getOrCreateChatRoom(sellerId = currentAdminId!!
                    , buyerId = buyerId)

                result.fold(
                    onSuccess = { room ->
                        withContext(Dispatchers.Main) {
                            chatRoomId = room.id
                            Log.d("chatmesssage","${room.id}")
//                            view?.updateChatRoom(room)
                            loadMessages()
                            markMessagesAsRead()
                            startOnlineStatusTracking(room.id)
                            isInitialized = true
                        }
                    },
                    onFailure = { e ->
                        view?.showError("Failed to initialize chat: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                view?.showError("Error initializing chat: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    private fun startOnlineStatusTracking(chatRoomId: String) {
        coroutineScope.launch {
            chatRepository.updateUserOnlineStatus(
                userId = currentAdminId!!,
                chatRoomId = chatRoomId,
                isOnline = true
            )
        }

        onlineStatusJob = coroutineScope.launch {
            try {
                chatRepository.getChatRoomOnlineStatus(chatRoomId)
                    .flowOn(Dispatchers.IO)
                    .collect { statusMap ->
                        withContext(Dispatchers.Main) {
                            view?.updateParticipantsStatus(statusMap)
                        }
                    }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError("Error tracking online status: ${e.message}")
                }
            }
        }
    }

    override fun loadMessages() {
        messagesJob?.cancel()
        messagesJob = coroutineScope.launch(Dispatchers.IO) {
            try {
                chatRoomId?.let { roomId ->
                    chatRepository.getMessages(roomId)
                        .collect { messages ->
                            withContext(Dispatchers.Main) {
                                if (view != null) {  // Check if view is still attached
                                    view?.showMessages(messages.sortedBy { it.timestamp })
                                    view?.scrollToBottom()
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError("Failed to load messages: ${e.message}")
                }
            }
        }
    }

    override fun sendMessage(content: String, type: MessageType) {
        if (content.isBlank() && type == MessageType.TEXT) return

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val message = Message(
                    id = UUID.randomUUID().toString(),
                    chatRoomId = chatRoomId ?: return@launch,
                    senderId = currentAdminId ?: return@launch,
                    receiverId = buyerId ?: return@launch,
                    content = content,
                    type = type,
                    timestamp = System.currentTimeMillis()
                )

                val result = chatRepository.sendMessage(message)

                result.fold(
                    onSuccess = {
                        withContext(Dispatchers.Main) {
                            view?.clearInput()
                        }
                    },
                    onFailure = { e ->
                        withContext(Dispatchers.Main) {
                            view?.showError("Failed to send message: ${e.message}")
                        }
                    }
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError("Failed to send message: ${e.message}")
                }
            }
        }
    }

    override fun sendMessageImage(mediaUrl: String, type: MessageType) {
        if (mediaUrl.isBlank() && type == MessageType.IMAGE) return

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val message = Message(
                    id = UUID.randomUUID().toString(),
                    chatRoomId = chatRoomId ?: return@launch,
                    senderId = currentAdminId ?: return@launch,
                    receiverId = buyerId ?: return@launch,
                    mediaUrl = mediaUrl,
                    type = type,
                    timestamp = System.currentTimeMillis()
                )

                val result = chatRepository.sendMessage(message)

                result.fold(
                    onSuccess = {
                        withContext(Dispatchers.Main) {
                            view?.clearInput()
                        }
                    },
                    onFailure = { e ->
                        withContext(Dispatchers.Main) {
                            view?.showError("Failed to send message: ${e.message}")
                        }
                    }
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError("Failed to send message: ${e.message}")
                }
            }
        }
    }

    override fun sendImage(uri: Uri) {
        coroutineScope.launch {
            try {
                val uploadResult = chatRepository.uploadImage(uri)

                uploadResult.fold(
                    onSuccess = { imageUrl ->
                        sendMessageImage(imageUrl, MessageType.IMAGE)
                    },
                    onFailure = { e ->
                        view?.showError("Failed to upload image: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                view?.showError("Error uploading image: ${e.message}")
            }
        }
    }

    override fun markMessagesAsRead() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                chatRoomId?.let { roomId ->
                    currentAdminId?.let { adminId ->
                        chatRepository.markMessageAsRead(roomId, adminId)
                    }
                }
            } catch (e: Exception) {
                view?.showError("Error marking messages as read: ${e.message}")
            }
        }
    }

    override fun onImagePickerClicked() {
        view?.showImagePicker()
    }

    override fun onResume() {
        coroutineScope.launch {
            chatRoomId?.let { roomId ->
                currentAdminId?.let { adminId ->
                    chatRepository.updateUserOnlineStatus(
                        userId = adminId,
                        chatRoomId = roomId,
                        isOnline = true
                    )
                }
            }
        }
    }

    override fun onPause() {
        coroutineScope.launch {
            chatRoomId?.let { roomId ->
                currentAdminId?.let { adminId ->
                    chatRepository.updateUserOnlineStatus(
                        userId = adminId,
                        chatRoomId = roomId,
                        isOnline = false
                    )
                }
            }
        }
    }

    override fun cleanup() {
        coroutineScope.launch {
            chatRoomId?.let {roomId->
                currentAdminId?.let { adminId->
                    chatRepository.updateUserOnlineStatus(
                        userId = adminId,
                        chatRoomId = roomId,
                        isOnline = false
                    )
                }
            }
        }

        messagesJob?.cancel()
        onlineStatusJob?.cancel()


        chatRoomId = null
        currentAdminId = null
        buyerId = null
        isInitialized = false
    }

}