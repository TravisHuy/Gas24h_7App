package com.nhathuy.gas24h_7app.data.model

data class OrderItem(
    val productId:String,
    val quantity:Int,
    val selectedVoucherId:String?=null
){
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "productId" to productId,
            "quantity" to quantity
        )
        selectedVoucherId?.let { map["selectedVoucherId"] = it }
        return map
    }
}