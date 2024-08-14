package com.nhathuy.gas24h_7app.data.model

data class Cart(
    val userId:String,
    val items:List<OrderItem> = listOf(),
    val totalAmount: Double=0.0
)
