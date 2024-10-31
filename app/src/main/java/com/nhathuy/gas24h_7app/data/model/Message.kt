package com.nhathuy.gas24h_7app.data.model

data class Message(
    val id:String = "",
    val senderId:String = "",
    val receiverId:String = "",
    val chatRoomId:String = "",
    val content:String ="",
    val type:MessageType = MessageType.TEXT,
    val status: MessageStatus = MessageStatus.SENDING,
    val timestamp: Long = System.currentTimeMillis(),
    val mediaUrl:String = "", // URL cho ảnh/file nếu có
    val mediaType:String = "", // MIME type của media
    val mediaSize: Long = 0,// Kích thước file theo bytes
    val replyTo:String = ""// ID của tin nhắn được reply (nếu có)
){
    constructor():this("","","","","",MessageType.TEXT,MessageStatus.SENDING,System.currentTimeMillis(),"","",0,"")

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "senderId" to senderId,
            "receiverId" to receiverId,
            "chatRoomId"  to chatRoomId,
            "content" to content,
            "type" to type.name,
            "status" to status.name,
            "timestamp" to timestamp,
            "mediaUrl" to mediaUrl,
            "mediaType" to mediaType,
            "mediaSize" to mediaSize,
            "replyTo" to replyTo
        )
    }
}
