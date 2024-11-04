package com.nhathuy.gas24h_7app.data.model

import java.util.Date

data class Notification(
    val id:String,
    val title:String,
    val content:String,
    val imageData: ByteArray?,
    val hotline:String,
    val date:Date
)
