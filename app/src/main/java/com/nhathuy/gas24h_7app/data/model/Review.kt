package com.nhathuy.gas24h_7app.data.model

data class Review(
    val id:String,
    val productId:String,
    val userId:String,
    val rating:Float,
    val comment:String,
    val timestamp:Long,
    val images: List<String> = emptyList(),
    val video: String
){
    constructor():this("","","",0f,"",0, emptyList(),"")
    fun toMap():Map<String,Any>{
        return hashMapOf("id" to id,
            "productId" to productId,
            "userId" to userId,
            "rating" to 0f,
            "comment" to comment,
            "timestamp" to 0,
            "images" to images,
            "video" to video
        )
    }
}
