package com.nhathuy.gas24h_7app.data.model

import java.math.BigDecimal
import java.util.Date

data class Voucher(
    val id: String,
    val code: String,
    val discountType: DiscountType,
    val discountValue: Double,
    val minOrderAmount: Double,
    val maxUsage: Int,
    val maxUsagePreUser: Int,
    val currentUsage: Int = 0,
    val startDate: Date,
    val endDate: Date,
    val isActive: Boolean = true,
    val applicableType : ApplicableType,
    val applicableProductIds: List<String> = listOf(),
    val userUsages: MutableMap<String, Int> = mutableMapOf()
) {
    constructor() : this(
        "",
        "",
        DiscountType.FIXED_AMOUNT,
        0.0,
        0.0,
        0,
        0,
        0,
        Date(),
        Date(),
        false,
        ApplicableType.ALL_PRODUCTS,
        listOf(),
        mutableMapOf()
    )

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
            "applicableType" to applicableType.name,
            "applicableProductIds" to applicableProductIds,
            "userUsages" to userUsages
        )
    }

    fun isValidForUser(userId: String): Boolean {
        val userUsageCount = userUsages[userId] ?: 0
        return userUsageCount < maxUsagePreUser
    }

    fun isApplicableToProducts(productIds: List<String>): Boolean {
        return when (applicableType) {
            ApplicableType.ALL_PRODUCTS -> true
            ApplicableType.DETAIL_PRODUCTS -> applicableProductIds.any { it in productIds }
        }
    }
}

enum class DiscountType {
    FIXED_AMOUNT,
    PERCENTAGE
}
enum class ApplicableType {
    ALL_PRODUCTS,
    DETAIL_PRODUCTS
}