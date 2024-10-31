package com.nhathuy.gas24h_7app.data.repository.impl

import android.net.Uri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.data.model.ChatRoom
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.MessageStatus
import com.nhathuy.gas24h_7app.data.repository.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

class ChatRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val dispatcher: CoroutineDispatcher
) : ChatRepository {


    companion object {
        private const val MESSAGES_COLLECTION = "messages"
        private const val CHAT_ROOMS_COLLECTION = "chatRooms"
        private const val CHAT_IMAGES_PATH = "chat_images"
    }


    override suspend fun sendMessage(message: Message): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection(MESSAGES_COLLECTION).document(message.id).set(message.toMap()).await()

            // Then update the chat room's last message
            updateLastMessage(message)

            // Increment unread count for receiver
            incrementUnreadCount(message.chatRoomId, message.receiverId)


            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateLastMessage(message: Message) {
        db.collection(CHAT_ROOMS_COLLECTION).document(message.chatRoomId)
            .update(
                mapOf(
                    "lastMessage" to message.toMap(),
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
    }

    private suspend fun incrementUnreadCount(chatRoomId: String, userId: String) {
        db.collection(CHAT_ROOMS_COLLECTION)
            .document(chatRoomId)
            .update("unreadCount.$userId", FieldValue.increment(1))
            .await()
    }

    override suspend fun getMessages(chatRoomId: String): Flow<List<Message>> =
        callbackFlow<List<Message>> {
            val subscription = db.collection(MESSAGES_COLLECTION)
                .whereEqualTo("chatRoomId", chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val messages = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(Message::class.java)
                    } ?: emptyList()

                    trySend(messages)
                }
            awaitClose { subscription.remove() }

        }.flowOn(dispatcher)

    override suspend fun createOrGetChatRoom(sellerId: String, buyerId: String): Result<ChatRoom> =
        withContext(dispatcher) {
            try {
                val participants = listOf(sellerId, buyerId).sorted()

                //check if chat room already exists
                val existingRoom = db.collection(CHAT_ROOMS_COLLECTION)
                    .whereEqualTo("participants ", participants)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()
                    ?.toObject(ChatRoom::class.java)

                if (existingRoom != null) {
                    return@withContext Result.success(existingRoom)
                }

                val newRoom = ChatRoom(
                    id = UUID.randomUUID().toString(),
                    participants=participants,
                    unreadCount = mapOf(
                        sellerId to 0,
                        buyerId to 0
                    ),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                db.collection(CHAT_ROOMS_COLLECTION).document(newRoom.id).set(newRoom.toMap()).await()

                Result.success(newRoom)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun updateMessageStatus(
        messageId: String,
        status: MessageStatus
    ): Result<Unit> = withContext(dispatcher){
        try {
            db.collection(MESSAGES_COLLECTION).document(messageId).update("status",status.name).await()
            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    override suspend fun getUnreadCount(chatRoomId: String, userId: String): Flow<Int> = callbackFlow{
        val subscription = db.collection(CHAT_ROOMS_COLLECTION)
            .document(chatRoomId)
            .addSnapshotListener{
                snapshot,error ->
                if(error!=null){
                    close(error)
                    return@addSnapshotListener
                }

                val unreadCount = snapshot?.get("unreadCount") as? Map<*,*>
                val count = (unreadCount?.get(userId) as? Number)?.toInt()?:0

                trySend(count)
            }

        awaitClose{subscription.remove()}
    }.flowOn(dispatcher)

    override suspend fun uploadImage(uri: Uri): Result<String> = withContext(dispatcher) {
        try {
            val filename = "${UUID.randomUUID()}.jpg"

            val imageRef = storage.reference
                .child(CHAT_IMAGES_PATH)
                .child(filename)

            val uploadTask = imageRef.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()

            Result.success(downloadUrl.toString())

        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    override suspend fun getRecentChats(userId: String): Flow<List<ChatRoom>> = callbackFlow {
        val subscription = db.collection(CHAT_ROOMS_COLLECTION)
            .whereArrayContains("participants", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val chatRooms = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatRoom::class.java)
                } ?: emptyList()

                trySend(chatRooms)
            }

        awaitClose { subscription.remove() }
    }.flowOn(dispatcher)

    override suspend fun markMessageAsRead(chatRoomId: String, userId: String): Result<Unit> = withContext(dispatcher){
        try {
            // Reset unread count for the user
            db.collection(CHAT_ROOMS_COLLECTION)
                .document(chatRoomId)
                .update(mapOf("unreadCount.$userId" to 0))
                .await()

            // Update status of unread messages
            val batch = db.batch()
            val unreadMessages = db.collection(MESSAGES_COLLECTION)
                .whereEqualTo("chatRoomId", chatRoomId)
                .whereEqualTo("receiverId", userId)
                .whereNotEqualTo("status", MessageStatus.READ.name)
                .get()
                .await()

            unreadMessages.documents.forEach { doc ->
                batch.update(doc.reference, "status", MessageStatus.READ.name)
            }

            batch.commit().await()
            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

}