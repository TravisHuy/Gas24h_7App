package com.nhathuy.gas24h_7app.data.model

data class ChatRoom(
    val id: String = "",
    val participants: List<String> = listOf(), // List user IDs
    val lastMessage: Message? = null,
    val unreadCount: Map<String, Int> = mapOf(), // userId -> số tin nhắn chưa đọc
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val metadata: Map<String, Any> = mapOf() // Các thông tin bổ sung
){

    constructor():this("", listOf(),null, mapOf(),System.currentTimeMillis(),System.currentTimeMillis(),
        mapOf()
    )

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "participants" to participants,
            "lastMessage" to lastMessage?.toMap(),
            "unreadCount" to unreadCount,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "metadata" to metadata
        )
    }
}
