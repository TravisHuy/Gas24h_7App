package com.nhathuy.gas24h_7app.data.model

data class OrderItem(
    val productId:String,
    val quantity:Int,
    val price:Double,
    val discountedPrice:Double
){

}