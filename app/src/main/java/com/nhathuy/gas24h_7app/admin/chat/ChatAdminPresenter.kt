package com.nhathuy.gas24h_7app.admin.chat

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
                    return@launch
                }

                chatRepository.getRecentChats(adminId)
                    .catch {
                        e->
                        view?.showError(e.message ?:"error loading chat rooms")
                    }
                    .collect{ rooms->
                        if(rooms.isEmpty()){
                            view?.showEmpty()
                        }
                        else{
                            view?.displayRooms(rooms)

                            rooms.forEach {
                                room ->
                                loadMessages(room.id)

                                room.participants.forEach {
                                    participantId ->
                                    if(participantId!=adminId){
                                        loadUserDetails(participantId)
                                    }
                                }
                            }
                        }
                    }
            }
            catch (e:Exception){
                view?.showError(e.message ?: "Error loading chat rooms")
            }
            finally {
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
                        val messageMap = messages.associateBy { it.id }
                        view?.updateChatRoom(messageMap, emptyMap())
                    }
            }
            catch (e:Exception){
                view?.showError(e.message ?: "Error loading messages")
            }
        }
    }

    override fun loadUserDetails(userId: String) {
        coroutineScope.launch {
            try {
                val user = userRepository.getUser(userId).getOrNull() ?: return@launch
                view?.updateChatRoom(emptyMap(), mapOf(userId to user))
            } catch (e: Exception) {
                view?.showError(e.message ?: "Error loading user details")
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