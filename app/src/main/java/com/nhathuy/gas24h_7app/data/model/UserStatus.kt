package com.nhathuy.gas24h_7app.data.model

data class UserStatus(
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis()
){
    constructor():this(false,System.currentTimeMillis())

    fun toMap():Map<String,Any>{
        return mapOf("isOnline" to isOnline,
            "lastSeen" to lastSeen
        )
    }
}