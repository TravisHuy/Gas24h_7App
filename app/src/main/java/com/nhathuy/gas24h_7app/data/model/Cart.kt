package com.nhathuy.gas24h_7app.data.model

data class Cart(
    val userId:String,
    var items:List<CartItem> = listOf(),
    var totalAmount: Double=0.0,
    var selectedVoucherId:String?=null
){
    constructor():this("", emptyList(),0.0,null)
}
