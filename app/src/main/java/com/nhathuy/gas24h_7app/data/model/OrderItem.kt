package com.nhathuy.gas24h_7app.data.model

data class OrderItem(
    val productId:String,
    val quantity:Int,
    val price:Double,
){
    constructor():this("",0,0.0)
    fun toMap(): Map<String, Any> {
        return mapOf(
            "productId" to productId,
            "quantity" to quantity,
            "price" to price
        )
    }
}