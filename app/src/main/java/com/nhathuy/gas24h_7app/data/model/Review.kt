package com.nhathuy.gas24h_7app.data.model

import java.util.Date

data class Review(
    val id:String,
    val productId:String,
    val userId:String,
    var rating:Float,
    var comment:String,
    var date:Date,
    var images: List<String> = emptyList(),
    var video: String,
    var reviewStatus:ReviewStatus
){
    constructor():this("","","",0f,"",Date(), emptyList(),"",ReviewStatus.FIVE_STARS)
    fun toMap():Map<String,Any>{
        return hashMapOf("id" to id,
            "productId" to productId,
            "userId" to userId,
            "rating" to rating,
            "comment" to comment,
            "date" to date,
            "images" to images,
            "video" to video,
            "reviewStatus" to reviewStatus.name
        )
    }
}
