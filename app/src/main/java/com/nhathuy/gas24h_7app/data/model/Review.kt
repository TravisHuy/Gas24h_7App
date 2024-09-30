package com.nhathuy.gas24h_7app.data.model

data class Review(
    val id:String,
    val productId:String,
    val userId:String,
    var rating:Float,
    var comment:String,
    var timestamp:Long,
    var images: List<String> = emptyList(),
    var video: String,
    var reviewStatus:ReviewStatus
){
    constructor():this("","","",0f,"",0, emptyList(),"",ReviewStatus.FIVE_STARS)
    fun toMap():Map<String,Any>{
        return hashMapOf("id" to id,
            "productId" to productId,
            "userId" to userId,
            "rating" to rating,
            "comment" to comment,
            "timestamp" to timestamp,
            "images" to images,
            "video" to video,
            "reviewStatus" to reviewStatus.name
        )
    }
}
