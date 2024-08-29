package com.nhathuy.gas24h_7app.data.model

import java.math.BigDecimal
import java.util.Date

data class Voucher(
    val id:String,
    val code:String,
    val discountType:DiscountType,
    val discountValue:Double,
    val minOrderAmount:Double,
    val maxUsage:Int,
    val maxUsagePreUser:Int,
    val currentUsage: Int = 0,
    val startDate:Date,
    val endDate:Date,
    val isActive:Boolean = false,
    val applicableProductIds: List<String> = listOf(),
    val isForAllProducts:Boolean =false
){
    constructor():this("","",DiscountType.FIXED_AMOUNT,0.0,0.0,0,0,0,Date(),Date(),false, listOf(),false)

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "code" to code,
            "discountType" to discountType.name,
            "discountValue" to discountValue,
            "minOrderAmount" to minOrderAmount,
            "maxUsage" to maxUsage,
            "maxUsagePreUser" to maxUsagePreUser,
            "currentUsage" to 0,
            "startDate" to startDate,
            "endDate" to endDate,
            "isActive" to isActive,
            "applicableProductIds" to applicableProductIds,
            "isForAllProducts" to isForAllProducts,
        )
    }
}
enum class DiscountType{
    FIXED_AMOUNT,
    PERCENTAGE
}
