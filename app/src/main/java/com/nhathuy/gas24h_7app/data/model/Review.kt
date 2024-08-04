package com.nhathuy.gas24h_7app.data.model

data class Review(
    val id:String,
    val productId:String,
    val userId:String,
    val rating:Float,
    val comment:String,
    val timestamp:Long,
    val images: List<String> = emptyList(),
    val videos: List<String> = emptyList()
){

}
