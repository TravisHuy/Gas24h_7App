package com.nhathuy.gas24h_7app.admin.chat

import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.repository.ChatRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatAdminPresenter @Inject constructor(private val chatRepository: ChatRepository, private val userRepository: UserRepository):ChatContract.Presenter {
    private var view:ChatContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private var currentMessages = mutableMapOf<String, Message>()
    private var currentUsers = mutableMapOf<String, User>()
    private var isLoadingMessages = false
    private var isLoadingUsers = false
    override fun attachView(view: ChatContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadRecentChat() {
        coroutineScope.launch {
            view?.showLoading()
            try {
                val adminId = userRepository.getUserAdminId().getOrNull() ?: run {
                    view?.showError("Could not find admin user")
                    view?.hideLoading()
                    return@launch
                }
                view?.onAdminIdLoaded(adminId!!)

                chatRepository.getRecentChats(adminId)
                    .catch {
                        e->
                        view?.showError(e.message ?:"error loading chat rooms")
                        view?.hideLoading()
                    }
                    .collect{ rooms->
                        if(rooms.isEmpty()){
                            view?.showEmpty()
                            view?.hideLoading()
                        }
                        else{
                            view?.displayRooms(rooms)


                            isLoadingMessages = false
                            isLoadingUsers = false


                            rooms.forEach {
                                room ->
                                isLoadingMessages = true
                                loadMessages(room.id)

                                room.participants.forEach {
                                    participantId ->
                                    if(participantId!=adminId){
                                        isLoadingUsers = true
                                        loadUserDetails(participantId)
                                    }
                                }
                            }
                        }
                        if (!isLoadingMessages && !isLoadingUsers) {
                            view?.hideLoading()
                        }
                    }
            }
            catch (e:Exception){
                view?.showError(e.message ?: "Error loading chat rooms")
                view?.hideLoading()
            }
        }
    }

    override fun loadMessages(chatRoomId: String) {
        coroutineScope.launch {
            try {
                chatRepository.getMessages(chatRoomId)
                    .collect{
                        messages ->
                        messages.maxByOrNull { it.timestamp }?.let { lastMessage ->
                            currentMessages[chatRoomId] = lastMessage
                            updateView()

                            isLoadingMessages = false
                            checkAndUpdateLoadingState()
                        }
                    }
            }
            catch (e:Exception){
                view?.showError(e.message ?: "Error loading messages")
                isLoadingMessages = false
                checkAndUpdateLoadingState()
            }
        }
    }

    private fun checkAndUpdateLoadingState() {
        if (!isLoadingMessages && !isLoadingUsers) {
            view?.hideLoading()
        }
    }

    private fun updateView() {
        view?.updateChatRoom(currentMessages.toMap(), currentUsers.toMap())
    }

    override fun loadUserDetails(userId: String) {
        coroutineScope.launch {
            try {
                val user = userRepository.getUser(userId).getOrNull() ?: run {
                    isLoadingUsers = false
                    checkAndUpdateLoadingState()
                    return@launch
                }
                currentUsers[userId] = user
                updateView()

                isLoadingUsers = false
                checkAndUpdateLoadingState()
            } catch (e: Exception) {
                view?.showError(e.message ?: "Error loading user details")
                isLoadingUsers = false
                checkAndUpdateLoadingState()
            }
        }
    }

    override fun markAsRead(chatRoomId: String, userId: String) {
        coroutineScope.launch {
            try {
                chatRepository.markMessageAsRead(chatRoomId, userId)
            } catch (e: Exception) {
                view?.showError(e.message ?: "Error marking messages as read")
            }
        }
    }


}