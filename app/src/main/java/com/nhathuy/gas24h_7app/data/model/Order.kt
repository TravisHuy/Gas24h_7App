package com.nhathuy.gas24h_7app.data.model

import java.util.Date

data class Order(
    val id:String,
    val userId:String,
    val items:List<OrderItem>,
    val totalAmount:Double,
    val discountAmount: Double,
    val appliedVoucherId:String?,
    val status:OrderStatus,
    val createdAt:Date,
    val updatedAt: Date,
){
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "items" to items.map { it.toMap() },
            "totalAmount" to totalAmount,
            "discountAmount" to discountAmount,
            "appliedVoucherId" to (appliedVoucherId ?: ""),
            "status" to status.name,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}
