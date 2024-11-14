package com.nhathuy.gas24h_7app.admin.chat.message

import android.net.Uri
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


    private var chatRoomId: String? = null
    private var currentAdminId: String? = null
    private var buyerId: String? = null
    override fun attachView(view: ChatMessageAdminContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun initialize(buyerId:String?) {
        coroutineScope.launch {
            try {
                view?.showLoading()

                currentAdminId = userRepository.getCurrentUserId() ?:  throw Exception("Admin not logged in")
                this@ChatMessageAdminPresenter.buyerId = buyerId
                view?.updateCurrentAdminId(currentAdminId!!)

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
//                            view?.updateChatRoom(room)
                            loadMessages()
                            markMessagesAsRead()
                        }
                    },
                    onFailure = { e ->
                        view?.showError("Failed to initialize chat: ${e.message}")
                    }
                )
                startObservingUserOnlineStatus(buyerId)
            } catch (e: Exception) {
                view?.showError("Error initializing chat: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    private fun startObservingUserOnlineStatus(userId: String) {
        onlineStatusJob = coroutineScope.launch {
            userId?.let { id ->
                chatRepository.getUserOnlineStatus(id).collect { status ->
                    withContext(Dispatchers.Main) {
                        view?.updateOnlineStatus(status)
                    }
                }
            }
        }
    }

    override fun loadMessages() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                chatRoomId?.let { roomId ->
                    chatRepository.getMessages(roomId)
                        .collect { messages ->
                            withContext(Dispatchers.Main) {
                                view?.showMessages(messages.sortedBy { it.timestamp })
                                view?.scrollToBottom()
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

    override fun cleanup() {
        coroutineScope.cancel()
        onlineStatusJob?.cancel()
    }
    override suspend fun getUserOnlineStatus(userId: String): Flow<UserStatus> {
        return chatRepository.getUserOnlineStatus(userId)
    }
}