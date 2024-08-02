package com.nhathuy.gas24h_7app.data.model

data class Review(
    val userId:String,
    val rating:Float,
    val comment:String,
    val timestamp:Long,
    val images: List<String> = emptyList(),
    val videos: List<String> = emptyList()
){
    constructor() : this("",0.0f,"",0L)
}
