package com.nhathuy.gas24h_7app.data.model

data class CartItem(
    val productId:String,
    var quantity:Int,
    var price:Double,
){
    constructor():this("",0,0.0)
}