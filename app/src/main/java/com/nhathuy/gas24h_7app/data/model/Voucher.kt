package com.nhathuy.gas24h_7app.data.model

import java.util.Date

data class Voucher(
    val id:String,
    val code:String,
    val discountPercentage:Double,
    val maxDiscountAmount:Double,
    val minOrderAmount:Double,
    val expirationDate:Date,
    val isActive:Boolean = false,
){
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "code" to code,
            "discountPercentage" to discountPercentage,
            "maxDiscountAmount" to maxDiscountAmount,
            "minOrderAmount" to minOrderAmount,
            "expirationDate" to expirationDate,
            "isActive" to isActive
        )
    }
}
